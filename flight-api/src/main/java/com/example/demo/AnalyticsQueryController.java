package com.example.demo;

import com.example.demo.graph.CustomNode;
import com.example.demo.repository.AirlineGuestRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@Slf4j
public class AnalyticsQueryController {

    final StreamBridge streamBridge;
    final AirlineGuestRepo airlineGuestRepo;
    final GraphSolver graphSolver;

    public AnalyticsQueryController(StreamBridge streamBridge,
                                    AirlineGuestRepo airlineGuestRepo,
                                    GraphSolver graphSolver) {
        this.streamBridge = streamBridge;
        this.airlineGuestRepo = airlineGuestRepo;
        this.graphSolver = graphSolver;
    }

    @GetMapping("/getMaxHops")
    public Mono<List<CustomNode>> maxHops() {
        List<CustomNode> nodes = graphSolver.getAllNodes();
        List<CustomNode> longest = null;
        int largest = 0;
        for (int i = 0; i < nodes.size(); i++) {
            for (int j = 0; j < nodes.size(); j++) {
                if (i != j) {
                    List<CustomNode> paths = graphSolver.shortestPathVertex(nodes.get(i), nodes.get(j));
                    if (paths.size() > largest) {
                        largest = paths.size();
                        longest = paths;
                    }
                }
            }
        }
        return Mono.just(longest);
    }
    @GetMapping("/path/shortest/{origin}/{dest}")
    public Mono<List<CustomNode>> shortestPath(@PathVariable("origin") String origin, @PathVariable("dest") String dest) {
        return Mono.just(graphSolver.shortestPathVertex(new CustomNode(origin, graphSolver.getValue(origin))
                , new CustomNode(dest,graphSolver.getValue(dest))));
    }


    @GetMapping("/all")
    public Mono<List<CustomNode>> allAirports() {
        return Mono.just(graphSolver.getAllNodes());
    }
}