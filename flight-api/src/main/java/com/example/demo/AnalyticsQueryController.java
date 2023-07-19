package com.example.demo;

import com.example.demo.dao.OriginList;
import com.example.demo.dto.AirportEdgeResult;
import com.example.demo.graph.CustomNode;
import com.example.demo.graph.CustomWeightEdge;
import com.example.demo.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
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
import java.time.LocalDate;
import java.time.ZoneOffset;
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

    final long lookAheadTime = 4 * 24 * 60 * 60;
    Comparator<GraphPath<CustomNode, CustomWeightEdge>> comparator = (o1, o2) -> {
        Long o1Value = o1.getEndVertex().getArrTime() - o1.getStartVertex().getArrTime();
        Long o2Value = o2.getEndVertex().getArrTime() - o2.getStartVertex().getArrTime();
        return o1Value.compareTo(o2Value);
    };

    public AnalyticsQueryController(StreamBridge streamBridge,
                                    AirlineGuestRepo airlineGuestRepo,
                                    OntimeRepo ontimeRepo,
                                    CarrierRepo carrierRepo,
                                    EdgeListRepo edgeListRepo,
                                    AirportEdgeResultRepo airportEdgeResultRepo) {
        this.streamBridge = streamBridge;
        this.airlineGuestRepo = airlineGuestRepo;
        this.ontimeRepo = ontimeRepo;
        this.carrierRepo = carrierRepo;
        this.edgeListRepo = edgeListRepo;
        this.airportEdgeResultRepo = airportEdgeResultRepo;
    }

    @GetMapping("/getMaxHops")
    public Mono<GraphPath<CustomNode, CustomWeightEdge>> shortLong() {
        List<OriginList> origins = edgeListRepo.getAllOrigin();
        int longest = 0;
        GraphPath<CustomNode, CustomWeightEdge> largest;
        for (int i = 0; i < origins.size(); i++) {
            for (int j = i; j < origins.size(); j++) {
                if (i != j) {
                    try {
                        GraphPath<CustomNode, CustomWeightEdge> route = getShortestTimePath(origins.get(i).getOrigin(),
                                origins.get(j).getOrigin(),
                                1672750800L);
                        if (route.getVertexList().size() > longest) {
                            longest = route.getVertexList().size();
                            largest = route;
                            System.out.println(largest.getEdgeList());
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
        }
        return Mono.empty();
    }

    @GetMapping("/path/shortest/{origin}/{dest}/{date}")
    public Mono<GraphPath<CustomNode, CustomWeightEdge>> dests(@PathVariable("origin") String origin,
                                                               @PathVariable("dest") String dest,
                                                               @PathVariable("date") String date) {
        long epochSeconds = LocalDate.parse(date, java.time.format.DateTimeFormatter.BASIC_ISO_DATE)
                .atStartOfDay().toEpochSecond(ZoneOffset.UTC);
        GraphPath<CustomNode, CustomWeightEdge> shortestPath = getShortestTimePath(origin, dest, epochSeconds);
        return Mono.just(shortestPath);
    }

    private GraphPath<CustomNode, CustomWeightEdge> getShortestTimePath(String origin, String finalDestination, Long arrTime) {
        Set<AirportEdgeResult> results = getSubList(airportEdgeResultRepo
                .findByOriginAndDepTimeBetween(origin, arrTime, arrTime + lookAheadTime,
                        PageRequest.of(0, 4000, Sort.by("depTime"))));
        List<GraphPath<CustomNode, CustomWeightEdge>> paths =
                results.stream().parallel()
                        .map(entry -> getCustomNodeCustomWeightEdgeGraphPath(origin, finalDestination, arrTime, entry))
                        .filter(Objects::nonNull).toList();
        TreeSet<GraphPath<CustomNode, CustomWeightEdge>> treeSet = new TreeSet<>(comparator);
        treeSet.addAll(paths);
        return treeSet.first();
    }

    @Nullable
    private GraphPath<CustomNode, CustomWeightEdge> getCustomNodeCustomWeightEdgeGraphPath(String origin,
                                                                                           String finalDestination,
                                                                                           Long arrTime,
                                                                                           AirportEdgeResult entry) {
        DirectedWeightedMultigraph<CustomNode, CustomWeightEdge> graph = new DirectedWeightedMultigraph<>(CustomWeightEdge.class);
        BellmanFordShortestPath<CustomNode, CustomWeightEdge> shortestPathAlgorithm = new BellmanFordShortestPath<>(graph);
        HashSet<CustomNode> vst = new HashSet<>();
        LinkedList<CustomNode> queue = new LinkedList<>();
        setVertexAndEdge(graph, entry);
        queue.add(entry.getDestinationNode());
        vst.add(entry.getDestinationNode());
        CustomNode finalNode = bfsTillDestination(queue, finalDestination, graph, vst);
        if (finalNode != null) {
            return shortestPathAlgorithm.getPath(new CustomNode(origin, arrTime), finalNode);
        }
        return null;
    }

    private CustomNode bfsTillDestination(LinkedList<CustomNode> queue, String finalDestination,
                                          DirectedWeightedMultigraph<CustomNode, CustomWeightEdge> graph,
                                          HashSet<CustomNode> vst) {
        CustomNode finalNode = null;
        int destinationVisitedTimes = 5;
        while (queue.size() != 0) {
            CustomNode popped = queue.pop();
            graph.addVertex(popped);
            Set<AirportEdgeResult> children = getSubList(airportEdgeResultRepo.
                    findByOriginAndDepTimeBetween(popped.getCode(), popped.getArrTime(), popped.getArrTime() + lookAheadTime / 4,
                            PageRequest.of(0, 4000, Sort.by("depTime"))));
            for (AirportEdgeResult child : children) {
                if (child.getDepTime() < popped.getArrTime()) {
                    continue;
                }
                if (child.getDestination().equals(finalDestination)) {
                    finalNode = child.getDestinationNode();
                    setVertexAndEdge(graph, child);
                    destinationVisitedTimes--;
                    if (destinationVisitedTimes == 0) {
                        queue = new LinkedList<>();
                        break;
                    }
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

    public Set<AirportEdgeResult> getSubList(Page<AirportEdgeResult> childs) {
        if (childs == null) {
            return Collections.EMPTY_SET;
        }
        return childs.get()
                .collect(groupingBy(AirportEdgeResult::getDestination,
                        minBy(comparingLong(AirportEdgeResult::getArrTime))))
                .values()
                .stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .sorted()
                .collect(toCollection(LinkedHashSet::new));
    }
}
