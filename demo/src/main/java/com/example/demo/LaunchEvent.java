package com.example.demo;

import com.clickhouse.client.ClickHouseClient;
import com.clickhouse.client.ClickHouseNode;
import com.clickhouse.data.ClickHouseCompression;
import com.clickhouse.data.ClickHouseFile;
import com.clickhouse.data.ClickHouseFormat;
import com.example.demo.entity.EdgeList;
import com.example.demo.repository.AirlineGuestRepo;
import com.example.demo.repository.EdgeListRepo;
import com.example.demo.repository.NewResultDao;
import com.example.demo.repository.OntimeRepo;
import org.hibernate.mapping.ClickHouseArrayMapper;
import org.jgrapht.alg.ConnectivityInspector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;


@Component
public class LaunchEvent implements ApplicationListener<ApplicationStartedEvent> {


    final StreamBridge streamBridge;
    final AirlineGuestRepo airlineGuestRepo;
    final OntimeRepo ontimeRepo;
    final EdgeListRepo edgeListRepo;

    final static String TABLE_NAME = "ontime";
    final static String SERVER_NAME = "http://localhost:8123/default";

    @Autowired
    public LaunchEvent(StreamBridge streamBridge, AirlineGuestRepo airlineGuestRepo, OntimeRepo ontimeRepo, EdgeListRepo edgeListRepo) {
        this.streamBridge = streamBridge;
        this.airlineGuestRepo = airlineGuestRepo;
        this.ontimeRepo = ontimeRepo;
        this.edgeListRepo = edgeListRepo;
    }

    private void loadData(String server, String table, String fileName) throws FileNotFoundException {
        File file = ResourceUtils.getFile("classpath:"+fileName);
        try {
            ClickHouseClient.load(
                    ClickHouseNode.of(server),
                    table,
                    ClickHouseFile.of(file,
                            ClickHouseCompression.GZIP, 7, ClickHouseFormat.CSVWithNames)).get();

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        try {
            loadData(SERVER_NAME, TABLE_NAME, "newSample.gz");
          //  loadData(SERVER_NAME, TABLE_NAME, "newSample1.gz");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        List<EdgeList> edgeList = ontimeRepo.getEdgeList().stream().map(EdgeList::builder).collect(Collectors.toList());
        edgeListRepo.saveAll(edgeList);
        GraphSolver graphSolver = new GraphSolver(edgeList);
        List<String> nodes = graphSolver.getAllNodes();
        List<String> longest = null;
        int largest = 0;
        for (int i =0; i<nodes.size(); i ++) {
            for (int j = 0; j<nodes.size(); j++) {
                if (i!=j) {
                  List<String> paths = graphSolver.shortestPathVertex(nodes.get(i), nodes.get(j));
                  if (paths.size() > largest ) {
                      largest = paths.size();
                      longest = paths;
                  }
                }
            }
        }
        System.out.println(graphSolver.connectedComponents().toString());
    }

}





