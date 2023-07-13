package com.example.demo;

import com.example.demo.dao.EdgeResultDao;
import com.example.demo.dao.OriginList;
import com.example.demo.dto.AirportEdgeResult;
import com.example.demo.graph.CustomNode;
import com.example.demo.graph.CustomWeightEdge;
import com.example.demo.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.BellmanFordShortestPath;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.*;

import static java.util.Comparator.comparingLong;
import static java.util.stream.Collectors.*;

@RestController
@Slf4j
public class AnalyticsQueryController {

    final StreamBridge streamBridge;
    final AirlineGuestRepo airlineGuestRepo;
    final OntimeRepo ontimeRepo;
    final CarrierRepo carrierRepo;
    final EdgeListRepo edgeListRepo;
    final AirportEdgeResultRepo airportEdgeResultRepo;

    public AnalyticsQueryController(StreamBridge streamBridge,
                                    AirlineGuestRepo airlineGuestRepo,
                                    OntimeRepo ontimeRepo, CarrierRepo carrierRepo,
                                    EdgeListRepo edgeListRepo,
                                    AirportEdgeResultRepo airportEdgeResultRepo) {
        this.streamBridge = streamBridge;
        this.airlineGuestRepo = airlineGuestRepo;
        this.ontimeRepo = ontimeRepo;
        this.carrierRepo = carrierRepo;
        this.edgeListRepo = edgeListRepo;
        this.airportEdgeResultRepo = airportEdgeResultRepo;
    }

    @GetMapping("/short")
    public Mono<GraphPath<CustomNode, CustomWeightEdge>> shortLong() {
        List<OriginList> origins = edgeListRepo.getAllOrigin();
        int longest = 0;
        GraphPath<CustomNode, CustomWeightEdge> largest;
        for (int i = 0; i < origins.size(); i++) {
            for (int j = i; j < origins.size(); j++) {
                if (i != j) {
                    try {
                        GraphPath<CustomNode, CustomWeightEdge> route = buildGraph(origins.get(i).getOrigin(),
                                origins.get(j).getOrigin(),
                                1672750800L);
                        if (route.getVertexList().size() > longest) {
                            longest = route.getVertexList().size();
                            largest = route;
                            System.out.println(largest.getVertexList());
                        }
                    }catch (Exception ignored) {}
                }
            }
        }
        return Mono.empty();

    }

    @GetMapping("/dests/{origin}/{dest}")
    public Mono<GraphPath<CustomNode, CustomWeightEdge>> dests(@PathVariable("origin") String origin, @PathVariable("dest") String dest) {
        System.out.print(origin);
        LinkedList<EdgeResultDao> result = new LinkedList<>();
        GraphPath<CustomNode, CustomWeightEdge> route = buildGraph(origin, dest, 1672750800L);
        return Mono.just(route);
    }

    private GraphPath<CustomNode, CustomWeightEdge> buildGraph(String origin, String finalDestination, Long arrTime) {
        DirectedWeightedMultigraph<CustomNode, CustomWeightEdge> graph = new DirectedWeightedMultigraph<>(CustomWeightEdge.class);
        BellmanFordShortestPath<CustomNode, CustomWeightEdge> shortestPathAlgorithm = new BellmanFordShortestPath<>(graph);
        HashSet<CustomNode> vst = new HashSet<>();
        LinkedList<CustomNode> queue = new LinkedList<>();
        Page<AirportEdgeResult> results = airportEdgeResultRepo
                .findByOriginAndDepTimeBetween(origin, arrTime, arrTime + 4 * 24 * 60 * 60,
                        PageRequest.of(0, 4000, Sort.by("depTime")));
        Set<AirportEdgeResult> subTempResult = getSubList(results);
        for (AirportEdgeResult entry : subTempResult) {
            setVertexAndEdge(graph, entry);
            queue.add(entry.getDestinationNode());
            vst.add(entry.getDestinationNode());
        }
        CustomNode finalNode = bfs(queue, finalDestination, graph, vst);
        return shortestPathAlgorithm.getPath(new CustomNode(origin, arrTime), finalNode);
    }

    private CustomNode bfs(LinkedList<CustomNode> queue, String finalDestination,
                           DirectedWeightedMultigraph<CustomNode, CustomWeightEdge> graph,
                           HashSet<CustomNode> vst) {
        CustomNode finalNode = null;
        while (queue.size() != 0) {
            CustomNode popped = queue.pop();
            graph.addVertex(popped);
            Page<AirportEdgeResult> childs = airportEdgeResultRepo.
                    findByOriginAndDepTimeBetween(popped.getCode(), popped.getArrTime(), popped.getArrTime() + 24 * 60 * 60,
                            PageRequest.of(0, 4000, Sort.by("depTime")));
            Set<AirportEdgeResult> subTempsResult = getSubList(childs);
            for (AirportEdgeResult child : subTempsResult) {
                if (child.getDepTime() < popped.getArrTime()) {
                    continue;
                }
                if (child.getDestination().equals(finalDestination)) {
                    finalNode = child.getDestinationNode();
                    setVertexAndEdge(graph, child);
                    queue = new LinkedList<>();
                    break;
                }
                if (!vst.contains(child.getDestinationNode())) {
                    vst.add(child.getDestinationNode());
                    queue.add(child.getDestinationNode());
                    setVertexAndEdge(graph, child);
                }
            }
        }
        return finalNode;
    }

    private void setVertexAndEdge(DirectedWeightedMultigraph<CustomNode, CustomWeightEdge> graph, AirportEdgeResult entry) {
        graph.addVertex(entry.getDestinationNode());
        graph.addVertex(entry.getOriginNode());
        CustomWeightEdge weightedEdge = graph.addEdge(entry.getOriginNode(), entry.getDestinationNode());
        weightedEdge.setAirline(entry.getAirline());
        weightedEdge.setStartTime(Instant.ofEpochSecond(entry.getDepTime()));
        weightedEdge.setEndTime(Instant.ofEpochSecond(entry.getArrTime()));
        graph.setEdgeWeight(weightedEdge, entry.getArrTime() - entry.getDepTime());
    }

    private Set<AirportEdgeResult> getSubList(Page<AirportEdgeResult> childs) {
        return childs.get()
                .collect(groupingBy(AirportEdgeResult::getDestination,
                        minBy(comparingLong(AirportEdgeResult::getArrTime)))).values()
                .stream().map(Optional::get).sorted().collect(toCollection(LinkedHashSet::new));
    }

}
