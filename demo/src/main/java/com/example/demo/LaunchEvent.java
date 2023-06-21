package com.example.demo;

import com.clickhouse.client.*;
import com.clickhouse.client.config.ClickHouseClientOption;
import com.clickhouse.client.config.ClickHouseDefaults;
import com.clickhouse.config.ClickHouseDefaultOption;
import com.clickhouse.config.ClickHouseOption;
import com.clickhouse.data.ClickHouseCompression;
import com.clickhouse.data.ClickHouseFile;
import com.clickhouse.data.ClickHouseFormat;
import com.clickhouse.data.value.ClickHouseMapValue;
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
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Component
public class LaunchEvent implements ApplicationListener<ApplicationStartedEvent> {

    @Autowired
    StreamBridge streamBridge;

    @Autowired
    GuestRepo guestRepo;
    @Autowired
    OntimeRepo ontimeRepo;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        System.out.println("Application is ready");

        try {


            ClickHouseClient.load(
                    ClickHouseNode.of("http://localhost:18123/default"),
                    "ontime",
                    // will parse file name later so that you don't have to specify compression and format
                    ClickHouseFile.of("C:\\Users\\siddhary87\\Downloads\\newSample.csv",
                            ClickHouseCompression.NONE, 0, ClickHouseFormat.CSVWithNames)).get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }


        IntStream.range(0, 1).parallel().forEach(x -> {
            System.out.println("published");
            streamBridge.send("order-channel", FlatBuffMessageBuilder.getMonsterMessage());
        });
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
        Optional<Guest> guest = guestRepo.findById(guestList.get(0).getId());
        System.out.println("xxxx" + guest.get().getIpAddress().getAddress());
        List<ResultDao> result = ontimeRepo.getResult();
        for (ResultDao resultDao : result) {
             System.out.println(resultDao.getOrigin() + " " + resultDao.getHops() + " " + resultDao.getTailNumber());
             System.out.println(ClickHouseArrayMapper.getStringArray(resultDao.getDepartures()));
            }

        List<NewResultDao> analysis = ontimeRepo.getAnalysis();
        for (NewResultDao resultDao : analysis) {
            System.out.println(resultDao.getAirline() + " " + resultDao.getFlightDate() + " " + resultDao.getTailNumber());
            System.out.println(ClickHouseArrayMapper.getStringArray(resultDao.getOrigins()));
            System.out.println(ClickHouseArrayMapper.getStringArray(resultDao.getDestinations()));
            System.out.println(ClickHouseArrayMapper.getIntArray(resultDao.getArrivals()));
            System.out.println(ClickHouseArrayMapper.getIntArray(resultDao.getDepartures()));
        }
    }
}



