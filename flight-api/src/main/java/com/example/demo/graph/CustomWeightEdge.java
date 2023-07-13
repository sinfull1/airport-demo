package com.example.demo.graph;


import org.jgrapht.graph.DefaultWeightedEdge;

import java.time.Instant;

public class CustomWeightEdge extends DefaultWeightedEdge {

    String airline;
    Instant startTime;
    Instant endTime;

    public void setAirline(String airline) {
        this.airline = airline;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
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
