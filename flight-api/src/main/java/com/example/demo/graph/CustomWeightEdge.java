package com.example.demo.graph;


import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.List;

public class CustomWeightEdge extends DefaultWeightedEdge {

    String airline;
    long startTime;
    long endTime;
    public void setAirline(String airline) {
        this.airline = airline;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    @Override
    public double getWeight() {
        return super.getWeight();
    }

    public String getAirline() {
        return airline;
    }



}
