package com.example.demo;

import com.example.demo.dto.*;
import com.example.demo.graph.CustomNode;
import com.example.demo.graph.CustomWeightEdge;
import com.example.demo.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.mapping.ClickHouseArrayMapper;
import org.jgrapht.GraphPath;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.*;

@RestController
@Slf4j
public class AnalyticsQueryController {

    final StreamBridge streamBridge;
    final AirlineGuestRepo airlineGuestRepo;
    final GraphSolver graphSolver;
    final OntimeRepo ontimeRepo;
    final CarrierRepo carrierRepo;
    final EdgeListRepo edgeListRepo;


    public AnalyticsQueryController(StreamBridge streamBridge,
                                    AirlineGuestRepo airlineGuestRepo,
                                    GraphSolver graphSolver, OntimeRepo ontimeRepo, CarrierRepo carrierRepo, EdgeListRepo edgeListRepo) {
        this.streamBridge = streamBridge;
        this.airlineGuestRepo = airlineGuestRepo;
        this.graphSolver = graphSolver;
        this.ontimeRepo = ontimeRepo;
        this.carrierRepo = carrierRepo;
        this.edgeListRepo = edgeListRepo;
    }

    private void init() {

    }

    @GetMapping("/getMaxHops")
    public Mono<List<MaxHopResultDto>> maxHops() {
        List<CustomNode> nodes = graphSolver.getAllNodes();
        List<CustomNode> longest = null;
        List<CustomWeightEdge> longestEdges = null;
        int largest = 0;
        for (int i = 0; i < nodes.size(); i++) {
            for (int j = 0; j < nodes.size(); j++) {
                if (i != j) {
                    GraphPath<CustomNode, CustomWeightEdge> paths = graphSolver.shortestPathVertex(nodes.get(i), nodes.get(j));
                    if (paths.getVertexList().size() > largest) {
                        largest = paths.getVertexList().size();
                        longest = paths.getVertexList();
                        longestEdges = paths.getEdgeList();
                    }
                }
            }
        }

        return Mono.just(craftResponse(longest, longestEdges));
    }

    private List<MaxHopResultDto> craftResponse(List<CustomNode> longest, List<CustomWeightEdge> longestEdges) {
        int length = longest.size();
        List<MaxHopResultDto> maxHopResultDtos = new ArrayList<>();
        for (int i = 0; i < length - 1; i++) {
            MaxHopResultDto maxHopResultDto = new MaxHopResultDto();
            maxHopResultDto.setSource(longest.get(i));
            maxHopResultDto.setDestination(longest.get(i + 1));
            maxHopResultDto.setFlightTime(longestEdges.get(i).getWeight());
            //    maxHopResultDto.setAirlines(longestEdges.get(i).getAirline().stream().map(GraphSolver::getCodeToAirline).toList());
            maxHopResultDtos.add(maxHopResultDto);
        }
        return maxHopResultDtos;
    }

    @GetMapping("/path/shortest/{origin}/{dest}")
    public Mono<GraphPath<CustomNode, CustomWeightEdge>> shortestPath(@PathVariable("origin") String origin, @PathVariable("dest") String dest) {
        return Mono.just(graphSolver.shortestPathVertex(new CustomNode(origin, graphSolver.getValue(origin))
                , new CustomNode(dest, graphSolver.getValue(dest))));
    }

    @GetMapping("/analysis")
    public Mono<List<AnalysisResultDto>> getAnalysis() {
        Iterable<AnalysisResultDao> analysisResultDaoItr = ontimeRepo.getAnalysis();
        List<AnalysisResultDto> analysisResultDtoList = new ArrayList<>();
        for (AnalysisResultDao analysisResultDao : analysisResultDaoItr) {
            AnalysisResultDto analysisResultDto = new AnalysisResultDto();
            analysisResultDto.setFlightDate(analysisResultDao.getFlightDate());
            analysisResultDto.setAirline(analysisResultDao.getAirline());
            analysisResultDto.setTailNumber(analysisResultDao.getTailNumber());
            List<String> origins = ClickHouseArrayMapper.getOrderedStringSet(analysisResultDao.getOrigins());
            List<Path> paths = new ArrayList<>();
            for (int i = 0; i < origins.size(); i++) {
                Path path = new Path();
                path.setOrigin(origins.get(i));
                path.setDestination(ClickHouseArrayMapper.getOrderedStringSet(analysisResultDao.getDests()).get(i));
                path.setArrival(ClickHouseArrayMapper.getOrderedIntegerSet(analysisResultDao.getArrivals()).get(i));
                path.setDeparture(ClickHouseArrayMapper.getOrderedIntegerSet(analysisResultDao.getDepartures()).get(i));
                paths.add(path);
            }
            analysisResultDto.setHops(analysisResultDao.getHops().intValue());
            analysisResultDto.setPaths(paths);
            analysisResultDtoList.add(analysisResultDto);
        }
        return Mono.just(analysisResultDtoList);
    }


    @GetMapping("/path/all/{origin}/{dest}")
    public Mono<List<List<CustomNode>>> allpaths(@PathVariable("origin") String origin, @PathVariable("dest") String dest) {
        return Mono.just(graphSolver.getAllPath(new CustomNode(origin, graphSolver.getValue(origin))
                , new CustomNode(dest, graphSolver.getValue(dest))));
    }

    @GetMapping("/all")
    public Mono<List<CustomNode>> allAirports() {
        return Mono.just(graphSolver.getAllNodes());
    }

    @GetMapping("/dests/{origin}/{dest}")
    public Mono<List<String>> dests(@PathVariable("origin") String origin, @PathVariable("dest") String dest) {
        System.out.print(origin);
        LinkedList<EdgeResultDao> result = new LinkedList<>();
        HashSet<TravNode> visited = new HashSet<>();
        LinkedList<TravNode> queue = new LinkedList<>();
        List<String> route = bfs(origin, dest, (System.currentTimeMillis() / 1000) - 100 * 30 * 24 * 3600, result, visited, queue, 0);
        return Mono.just(route);

    }

    @GetMapping("/connected")
    public Mono<List<Set<CustomNode>>> connectComponents() {
        return Mono.just(graphSolver.connectComponents());
    }

    private List<String> bfs(String origin, String finalDestination, Long arrTime, LinkedList<EdgeResultDao> lists,
                     Set<TravNode> vst, LinkedList<TravNode> queue, int depth) {
        TravNode start = new TravNode(origin, arrTime);
        vst.add(start);
        queue.add(start);
        Map<String, String> backTrack = new HashMap<>();
        while (queue.size() != 0) {
            TravNode popped = queue.pop();
            List<EdgeResultDao> results = edgeListRepo.getDestinations(popped.getDestination(), popped.getArrTime());
            for (EdgeResultDao child : results) {
                if (child.getDestination().equals(finalDestination)) {
                    backTrack.put(child.getDestination(), popped.getDestination());
                    queue = new LinkedList<>();
                    break;
                }
                if (!vst.contains(child)) {
                    backTrack.put(child.getDestination(), popped.getDestination());
                    System.out.println(popped.getDestination() + " " + child.getDestination());
                    TravNode childNode = new TravNode(child.getDestination(), child.getArrTime());
                    vst.add(childNode);
                    queue.add(childNode);
                }
            }
        }
        List<String> route = new ArrayList<>();
        String desti = finalDestination;
        route.add(desti);
        while (desti != null) {
            desti = backTrack.get(desti);
            route.add(desti);
        }
        return route;


    }
}
