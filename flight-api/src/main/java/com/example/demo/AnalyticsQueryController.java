package com.example.demo;

import com.example.demo.dto.AnalysisResultDto;
import com.example.demo.dto.Path;
import com.example.demo.graph.CustomNode;
import com.example.demo.repository.AirlineGuestRepo;
import com.example.demo.repository.AnalysisResultDao;
import com.example.demo.repository.OntimeRepo;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.mapping.ClickHouseArrayMapper;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@Slf4j
public class AnalyticsQueryController {

    final StreamBridge streamBridge;
    final AirlineGuestRepo airlineGuestRepo;
    final GraphSolver graphSolver;
    final OntimeRepo ontimeRepo;

    public AnalyticsQueryController(StreamBridge streamBridge,
                                    AirlineGuestRepo airlineGuestRepo,
                                    GraphSolver graphSolver, OntimeRepo ontimeRepo) {
        this.streamBridge = streamBridge;
        this.airlineGuestRepo = airlineGuestRepo;
        this.graphSolver = graphSolver;
        this.ontimeRepo = ontimeRepo;
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

    @GetMapping("/connected")
    public Mono<List<Set<CustomNode>>> connectComponents() {
        return Mono.just(graphSolver.connectComponents());
    }
}