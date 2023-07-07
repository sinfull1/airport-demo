package com.example.demo.graph;

import org.jgrapht.graph.DefaultWeightedEdge;

public class CustomWeightEdge extends DefaultWeightedEdge {


    double weight;

    @Override
    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

}
