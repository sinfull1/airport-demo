package com.example.demo;

import com.clickhouse.client.ClickHouseClient;
import com.clickhouse.client.ClickHouseNode;
import com.clickhouse.data.ClickHouseCompression;
import com.clickhouse.data.ClickHouseFile;
import com.clickhouse.data.ClickHouseFormat;
import com.example.demo.dto.AirportEdgeResult;
import com.example.demo.entity.EdgeList;
import com.example.demo.repository.AirlineGuestRepo;
import com.example.demo.repository.AirportEdgeResultRepo;
import com.example.demo.repository.EdgeListRepo;
import com.example.demo.repository.OntimeRepo;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class LaunchEvent implements ApplicationListener<ApplicationStartedEvent> {
    final AirportEdgeResultRepo airportEdgeResultRepo;

    final StreamBridge streamBridge;
    final AirlineGuestRepo airlineGuestRepo;
    final OntimeRepo ontimeRepo;
    final EdgeListRepo edgeListRepo;
    final static String TABLE_NAME = "ontime";
    final static String SERVER_NAME = "http://localhost:8123/default";

    @Autowired
    public LaunchEvent(AirportEdgeResultRepo airportEdgeResultRepo, StreamBridge streamBridge, AirlineGuestRepo airlineGuestRepo,
                       OntimeRepo ontimeRepo, EdgeListRepo edgeListRepo) {
        this.airportEdgeResultRepo = airportEdgeResultRepo;
        this.streamBridge = streamBridge;
        this.airlineGuestRepo = airlineGuestRepo;
        this.ontimeRepo = ontimeRepo;
        this.edgeListRepo = edgeListRepo;

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
           log.error(e.getLocalizedMessage());
        }
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        try {
            long length = System.currentTimeMillis();
            log.info(String.valueOf(length));
            loadData(SERVER_NAME, "carrier", "carriers.csv", ClickHouseCompression.NONE, 0, ClickHouseFormat.CSVWithNames);
            loadData(SERVER_NAME, TABLE_NAME, "flight1.gz", ClickHouseCompression.GZIP, 7, ClickHouseFormat.CSVWithNames);
            //loadData(SERVER_NAME, TABLE_NAME, "flight2.gz", ClickHouseCompression.GZIP, 7, ClickHouseFormat.CSVWithNames);
            //loadData(SERVER_NAME, TABLE_NAME, "flight3.gz", ClickHouseCompression.GZIP, 7, ClickHouseFormat.CSVWithNames);
            //loadData(SERVER_NAME, TABLE_NAME, "flight4.gz", ClickHouseCompression.GZIP, 7, ClickHouseFormat.CSVWithNames);
            log.info(String.valueOf(System.currentTimeMillis()-length));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        List<EdgeList> edgeList = ontimeRepo.getEdgeListWithDeps().stream().map(EdgeList::builderV2).collect(Collectors.toList());
        edgeListRepo.saveAll(edgeList);
        List<AirportEdgeResult> airportEdgeResults = edgeListRepo.getDestinationsAll().stream()
                .map(AirportEdgeResult::builder)
                .collect(Collectors.toList());
        airportEdgeResultRepo.saveAll(airportEdgeResults);

    }
}





