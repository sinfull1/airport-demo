package com.example.demo;

import com.clickhouse.client.ClickHouseClient;
import com.clickhouse.client.ClickHouseNode;
import com.clickhouse.data.ClickHouseCompression;
import com.clickhouse.data.ClickHouseFile;
import com.clickhouse.data.ClickHouseFormat;
import com.example.demo.repository.AirlineGuestRepo;
import com.example.demo.repository.NewResultDao;
import com.example.demo.repository.OntimeRepo;
import org.hibernate.mapping.ClickHouseArrayMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutionException;


@Component
public class LaunchEvent implements ApplicationListener<ApplicationStartedEvent> {


    final StreamBridge streamBridge;
    final AirlineGuestRepo airlineGuestRepo;
    final OntimeRepo ontimeRepo;

    final static String FILE_NAME = "C:\\Users\\siddhary87\\Downloads\\newSample.csv";
    final static String TABLE_NAME = "ontime";
    final static String SERVER_NAME = "http://localhost:8123/default";

    @Autowired
    public LaunchEvent(StreamBridge streamBridge, AirlineGuestRepo airlineGuestRepo, OntimeRepo ontimeRepo) {
        this.streamBridge = streamBridge;
        this.airlineGuestRepo = airlineGuestRepo;
        this.ontimeRepo = ontimeRepo;
    }

    private void loadData(String server, String table, String fileName) {
        try {
            ClickHouseClient.load(
                    ClickHouseNode.of(server),
                    table,
                    ClickHouseFile.of(fileName,
                            ClickHouseCompression.NONE, 0, ClickHouseFormat.CSVWithNames)).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }



    public void publishMessages() {
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        System.out.println("Application is ready");
        loadData(SERVER_NAME, TABLE_NAME, FILE_NAME);
        publishMessages();
        List<NewResultDao> analysis = ontimeRepo.getAnalysis();
        for (NewResultDao resultDao : analysis) {
            System.out.println(resultDao.getAirline() + " " + resultDao.getFlightDate() + " " + resultDao.getTailNumber());
            System.out.println(ClickHouseArrayMapper.getOrderedStringSet(resultDao.getOrigins()));
            System.out.println(ClickHouseArrayMapper.getOrderedStringSet(resultDao.getGroups()));
            System.out.println(ClickHouseArrayMapper.getOrderedIntegerSet(resultDao.getArrivals()));
            System.out.println(ClickHouseArrayMapper.getOrderedStringSet(resultDao.getTops()));
        }
    }
}



