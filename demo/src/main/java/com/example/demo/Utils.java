package com.example.demo;

import com.example.demo.entity.EdgeList;
import com.example.demo.graph.CustomWeightEdge;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;

import java.util.List;

public class Utils {

    public static void shortestPath(List<EdgeList> edgeList, String origin, String destination) {
        Graph<String, CustomWeightEdge> graph = new DefaultDirectedWeightedGraph<>(CustomWeightEdge.class);
        for (EdgeList edge : edgeList) {
            graph.addVertex(edge.getOrigin());
            graph.addVertex(edge.getDestination());
            CustomWeightEdge weightedEdge = graph.addEdge(edge.getOrigin(), edge.getDestination());
            weightedEdge.setWeight(edge.getTimes());
        }
        DijkstraShortestPath<String, CustomWeightEdge> shortestPathAlgorithm =
                new DijkstraShortestPath<>(graph);
        GraphPath<String, CustomWeightEdge> shortestPath = shortestPathAlgorithm.getPath(origin, destination);
        if (shortestPath != null) {
            List<String> pathNodes = shortestPath.getVertexList();
            System.out.println(pathNodes.toString());
        } else {
            System.out.println("No path exists from");
        }
    }

}
