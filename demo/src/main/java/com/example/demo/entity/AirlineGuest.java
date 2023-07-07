package com.example.demo.entity;


import io.hypersistence.utils.hibernate.type.array.ListArrayType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalTime;
import java.util.*;

@Entity
@Getter
@Setter
@Table(name = "guests")
@TableEngine(name = "MergeTree", ttlColumn = "dateColumn", ttlDuration = "2 DAY")
@PartitionKey(columns = {"guestType"})
public class AirlineGuest implements Serializable {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    @ColumnDefault("999999999")
    private String phone;

    @Column(name = "dateColumn", nullable = false)
    private Date dateColumn;

    @Column(name = "timeColumn")
    private Time timeColumn;
    @Column(name = "timeStampColumn")
    private Timestamp timeStampColumn;
    @Enumerated(EnumType.STRING)
    @Column(name = "guestType")
    GuestType guestType;

    @Enumerated(EnumType.STRING)
    @Column(name = "origin")
    AirportEnum origin;

    @Enumerated(EnumType.STRING)
    @Column(name = "dest")
    AirportEnum dest;

    @Type(ListArrayType.class)
    @Column(
            name = "reservations",
            columnDefinition = "Array(String)"
    )
    private List<String> reservations;


    public static AirlineGuest getRandomGuest() {
        Random random = new Random();
        AirlineGuest guest = new AirlineGuest();
        guest.setEmail("Email");
        guest.setName(UUID.randomUUID().toString().substring(0, 10));
        guest.setOrigin(AirportEnum.values()[random.nextInt((int) AirportEnum.values().length / 2)]);
        guest.setDest(AirportEnum.values()[AirportEnum.values().length / 2 + random.nextInt(AirportEnum.values().length / 2) - 1]);
        guest.setReservations(Arrays.asList(UUID.randomUUID().toString()));
        guest.setDateColumn(java.sql.Date.from(Instant.ofEpochMilli(System.currentTimeMillis())));
        guest.setTimeColumn(Time.valueOf(LocalTime.MIDNIGHT));
        guest.setTimeStampColumn(Timestamp.from(Instant.ofEpochMilli(System.currentTimeMillis())));
        guest.setGuestType(GuestType.values()[random.nextInt(1)]);
        guest.setPhone("999999999");
        return guest;
    }

    // constructors, getters, and setters omitted for brevity
}