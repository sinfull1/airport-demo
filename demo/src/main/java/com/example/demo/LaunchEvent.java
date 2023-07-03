package com.example.demo;

import com.clickhouse.client.*;
import com.clickhouse.data.ClickHouseCompression;
import com.clickhouse.data.ClickHouseFile;
import com.clickhouse.data.ClickHouseFormat;
import com.example.demo.builder.FlatBuffMessageBuilder;
import com.example.demo.entity.Guest;
import com.example.demo.entity.GuestType;
import com.example.demo.repository.*;
import io.hypersistence.utils.hibernate.type.basic.Inet;
import org.hibernate.mapping.ClickHouseArrayMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;


@Component
public class LaunchEvent implements ApplicationListener<ApplicationStartedEvent> {


    final StreamBridge streamBridge;
    final GuestRepo guestRepo;
    final OntimeRepo ontimeRepo;

    final static String FILE_NAME = "C:\\Users\\siddhary87\\Downloads\\newSample.csv";
    final static String TABLE_NAME = "ontime";
    final static String SERVER_NAME = "http://localhost:18123/default";

    @Autowired
    public LaunchEvent(StreamBridge streamBridge, GuestRepo guestRepo, OntimeRepo ontimeRepo) {
        this.streamBridge = streamBridge;
        this.guestRepo = guestRepo;
        this.ontimeRepo = ontimeRepo;
    }

    private void loadData(String server, String table, String fileName ) {
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

    private void  populateGuestTable() {
        Random random = new Random();
        List<Guest> guestList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Guest guest = new Guest();
            guest.setEmail("Email");
            guest.setShortInt((short) 123);
            guest.setName("Name");
            guest.setIpAddress(new Inet("11.13.44.33"));
            guest.setReservations(Arrays.asList("123", "1237"));
            guest.setStart(Date.from(Instant.ofEpochMilli(System.currentTimeMillis() + random.nextInt(100000))));
            guest.setGuestType(GuestType.NORMAL);
            guest.setPhone("rfkjndfkvj");
            guestList.add(guest);
        }
        guestRepo.saveAll(guestList);
    }

    public void publishMessages() {
        IntStream.range(0, 1).parallel().forEach(x -> {
            System.out.println("published");
            streamBridge.send("order-channel", FlatBuffMessageBuilder.getMonsterMessage());
        });
    }
    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        System.out.println("Application is ready");
        loadData(SERVER_NAME, TABLE_NAME, FILE_NAME);
        publishMessages();
        populateGuestTable();

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



