package com.example.demo;

import com.example.demo.entity.EdgeList;
import com.example.demo.graph.CustomWeightEdge;
import lombok.extern.slf4j.Slf4j;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Slf4j
public class GraphSolver {

    DijkstraShortestPath<String, CustomWeightEdge> shortestPathAlgorithm ;
    Graph<String, CustomWeightEdge>  graph ;
    List<EdgeList> edgeList = null;
    public GraphSolver(List<EdgeList> edgeList) {
          this.edgeList = edgeList;
          this.init();
    }

    private void init() {
        graph = new DefaultDirectedWeightedGraph<>(CustomWeightEdge.class);
        for (EdgeList edge : this.edgeList) {
            graph.addVertex(edge.getOrigin());
            graph.addVertex(edge.getDestination());
            CustomWeightEdge weightedEdge = graph.addEdge(edge.getOrigin(), edge.getDestination());
            weightedEdge.setWeight(edge.getTimes());
        }
        shortestPathAlgorithm = new DijkstraShortestPath<>(graph);
    }

    public List<String> getAllNodes() {
        HashSet<String> nodeSet = new HashSet<>();
        for (EdgeList edge : this.edgeList) {
           nodeSet.add(edge.getOrigin());
           nodeSet.add(edge.getDestination());
        }
        return  nodeSet.stream().toList();
    }


    public List<String> shortestPathVertex(String origin, String destination) {
        GraphPath<String, CustomWeightEdge> shortestPath = shortestPathAlgorithm.getPath(origin, destination);
        if (shortestPath != null) {
            return shortestPath.getVertexList();
        } else {
            return Collections.emptyList();
        }
    }

}
