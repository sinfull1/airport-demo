package com.example.demo.graph;


import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.List;

public class CustomWeightEdge extends DefaultWeightedEdge {


    double weight;
    List<String> airline;

    @Override
    public double getWeight() {
        return weight;
    }

    public List<String> getAirline() {
        return airline;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setAirline(List<String> airline) {
        this.airline = airline;
    }

}
