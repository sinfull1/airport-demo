package com.example.demo.entity;


import com.example.demo.Utils;
import com.example.demo.dao.EdgeListDao;
import com.example.demo.types.LongArrayUserType;
import com.example.demo.types.StringArrayUserType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

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


    @Column(name = "originCity", nullable = false)
    private String originCity;

    @Column(name = "destination", nullable = false)
    private String destination;

    @Column(name = "destCity", nullable = false)
    private String destCity;


    @Type(StringArrayUserType.class)
    @Column(
            name = "airline",
            columnDefinition = "Array(String)"
    )
    private String[] airline;

    @Type(LongArrayUserType.class)
    @Column(
            name = "arrTimes",
            columnDefinition = "Array(UInt64)"
    )
    private long[] arrTimes;

    @Type(LongArrayUserType.class)
    @Column(
            name = "depTimes",
            columnDefinition = "Array(UInt64)"
    )
    private long[] depTimes;

    public static EdgeList builderV2(EdgeListDao edgeListDao) {
        EdgeList edgeList = new EdgeList();
        edgeList.setOrigin(edgeListDao.getOrigin());
        edgeList.setArrTimes(Utils.getIntegerArray(edgeListDao.getArrTimes()));
        edgeList.setDestination(edgeListDao.getDestination());
        edgeList.setOriginCity(edgeListDao.getOriginCity());
        edgeList.setDepTimes(Utils.getIntegerArray(edgeListDao.getDepTimes()));
        edgeList.setAirline(Utils.getStringArray(edgeListDao.getAirline()));
        edgeList.setDestCity(edgeListDao.getDestCity());
        return edgeList;
    }


}
