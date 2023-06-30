package com.example.demo.entity;


import io.hypersistence.utils.hibernate.type.array.ListArrayType;
import io.hypersistence.utils.hibernate.type.basic.ClickHouseInetType;
import io.hypersistence.utils.hibernate.type.basic.Inet;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.springframework.data.jpa.repository.query.AbstractJpaQuery;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "guests")
@TableEngine(name = "MergeTree")
@PartitionKey(columns = {"guestType, shortInt"})
public class Guest {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;


    @Column(name = "shortInt")
    private Short shortInt;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "start")

    private Date start;
    @Enumerated(EnumType.STRING)
    @Column(name = "guestType")
    GuestType guestType;

    @Type(ClickHouseInetType.class)
    @Column(
            name = "ipadress",
            columnDefinition = "IPv4"
    )
    private Inet ipAddress;

    @Type(ListArrayType.class)
    @Column(
            name = "reservations",
            columnDefinition = "Array(String)"
    )
    private List<String> reservations;


    // constructors, getters, and setters omitted for brevity
}