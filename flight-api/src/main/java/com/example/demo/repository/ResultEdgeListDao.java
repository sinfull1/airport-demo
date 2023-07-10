package com.example.demo.repository;

import com.example.demo.graph.CustomNode;

public interface ResultEdgeListDao {


    String getDestination();

    String getDestCity();

    Object[] getAirline();

    Object[] getArrTimes();

    Object[] getDepTimes();

    default CustomNode getDestNode() {
        return new CustomNode(this.getDestination(), this.getDestCity());
    }
}
