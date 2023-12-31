package com.example.demo;

import com.clickhouse.client.ClickHouseClient;
import com.clickhouse.client.ClickHouseNode;
import com.clickhouse.data.ClickHouseCompression;
import com.clickhouse.data.ClickHouseFile;
import com.clickhouse.data.ClickHouseFormat;
import com.example.demo.entity.EdgeList;
import com.example.demo.repository.AirlineGuestRepo;
import com.example.demo.repository.EdgeListRepo;
import com.example.demo.repository.OntimeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;


@Component
public class LaunchEvent implements ApplicationListener<ApplicationStartedEvent> {


    final StreamBridge streamBridge;
    final AirlineGuestRepo airlineGuestRepo;
    final OntimeRepo ontimeRepo;
    final EdgeListRepo edgeListRepo;

    final GraphSolver graphSolver;
    final static String TABLE_NAME = "ontime";
    final static String SERVER_NAME = "http://localhost:8123/default";

    @Autowired
    public LaunchEvent(StreamBridge streamBridge, AirlineGuestRepo airlineGuestRepo,
                       OntimeRepo ontimeRepo, EdgeListRepo edgeListRepo, GraphSolver graphSolver) {
        this.streamBridge = streamBridge;
        this.airlineGuestRepo = airlineGuestRepo;
        this.ontimeRepo = ontimeRepo;
        this.edgeListRepo = edgeListRepo;
        this.graphSolver = graphSolver;
    }

    private void loadData(String server, String table, String fileName,
                          ClickHouseCompression compression, int level,
                          ClickHouseFormat format) throws FileNotFoundException {
        File file = ResourceUtils.getFile("classpath:" + fileName);
        try {
            ClickHouseClient.load(
                    ClickHouseNode.of(server),
                    table,
                    ClickHouseFile.of(file,
                            compression, level, format)).get();

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        try {
            loadData(SERVER_NAME, "carrier", "carriers.csv", ClickHouseCompression.NONE, 0, ClickHouseFormat.CSVWithNames);
            loadData(SERVER_NAME, TABLE_NAME, "flight1.gz", ClickHouseCompression.GZIP, 7, ClickHouseFormat.CSVWithNames);
            loadData(SERVER_NAME, TABLE_NAME, "flight2.gz", ClickHouseCompression.GZIP, 7, ClickHouseFormat.CSVWithNames);
            loadData(SERVER_NAME, TABLE_NAME, "flight3.gz", ClickHouseCompression.GZIP, 7, ClickHouseFormat.CSVWithNames);
            loadData(SERVER_NAME, TABLE_NAME, "flight4.gz", ClickHouseCompression.GZIP, 7, ClickHouseFormat.CSVWithNames);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        List<EdgeList> edgeList = ontimeRepo.getEdgeList().stream().map(EdgeList::builder).collect(Collectors.toList());
        edgeListRepo.saveAll(edgeList);
        graphSolver.init();
    }
}





