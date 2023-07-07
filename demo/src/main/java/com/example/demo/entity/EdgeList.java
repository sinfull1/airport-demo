package com.example.demo.entity;


import com.example.demo.graph.CustomNode;
import com.example.demo.repository.EdgeListDao;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

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

    @Column(name = "origin")
    private String origin;

    @Column(name = "originCity")
    private String originCity;

    @Column(name = "destination")
    private String destination;

    @Column(name = "destCity")
    private String destCity;

    @Column(name = "times")
    private Float times;


    public static EdgeList builder(EdgeListDao edgeListDao) {
        EdgeList edgeList = new EdgeList();
        edgeList.setOrigin(edgeListDao.getOrigin());
        edgeList.setTimes(edgeListDao.getTimes());
        edgeList.setDestination(edgeListDao.getDestination());
        edgeList.setOriginCity(edgeListDao.getOrigCity());
        edgeList.setDestCity(edgeListDao.getDestCity());
        return edgeList;
    }


    public CustomNode getOriginNode() {
        return new CustomNode(this.origin, this.originCity);
    }
    public CustomNode getDestNode(){
        return new CustomNode(this.destination, this.destCity);
    }
}
