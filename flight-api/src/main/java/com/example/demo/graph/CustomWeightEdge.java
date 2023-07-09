package com.example.demo.graph;


import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.List;

public class CustomWeightEdge extends DefaultWeightedEdge {

    String airline;
    int startTime;
    int endTime;
    public void setAirline(String airline) {
        this.airline = airline;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
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
