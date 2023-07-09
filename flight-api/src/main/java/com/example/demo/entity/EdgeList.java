package com.example.demo.entity;


import com.example.demo.graph.CustomNode;
import com.example.demo.repository.EdgeListDao;
import io.hypersistence.utils.hibernate.type.array.ListArrayType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.mapping.ClickHouseArrayMapper;

import java.util.Arrays;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "edges")
@TableEngine(name = "MergeTree")
public class EdgeList {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    @Column(name = "origin", nullable = false)
    private String origin;

    @Column(name = "originCity",nullable = false)
    private String originCity;

    @Column(name = "destination",nullable = false)
    private String destination;

    @Column(name = "destCity",nullable = false)
    private String destCity;

    @Type(ListArrayType.class)
    @Column(
            name = "airline",
            columnDefinition = "Array(String)"
    )
    private List<String> airline;
    @Column(name = "times",nullable = false)
    private Float times;


    public static EdgeList builder(EdgeListDao edgeListDao) {
        EdgeList edgeList = new EdgeList();
        edgeList.setOrigin(edgeListDao.getOrigin());
        edgeList.setTimes(edgeListDao.getTimes());
        edgeList.setDestination(edgeListDao.getDestination());
        edgeList.setOriginCity(edgeListDao.getOrigCity());
        edgeList.setAirline(ClickHouseArrayMapper.getOrderedStringSet(edgeListDao.getAirline()));
        edgeList.setDestCity(edgeListDao.getDestCity());
        return edgeList;
    }
    public static EdgeList builderV2(EdgeListDao edgeListDao) {
        EdgeList edgeList = new EdgeList();
        edgeList.setOrigin(edgeListDao.getOrigin());
        edgeList.setTimes(edgeListDao.getTimes());
        edgeList.setDestination(edgeListDao.getDestination());
        edgeList.setOriginCity(edgeListDao.getOrigCity());
        edgeList.setAirline(ClickHouseArrayMapper.getOrderedStringSet(edgeListDao.getAirline()));
        edgeList.setDestCity(edgeListDao.getDestCity());
        return edgeList;
    }

    public CustomNode getOriginNode() {
        return new CustomNode(this.origin, this.originCity);
    }

    public CustomNode getDestNode() {
        return new CustomNode(this.destination, this.destCity);
    }
}
