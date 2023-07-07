package com.example.demo;

import com.example.demo.entity.EdgeList;
import com.example.demo.graph.CustomNode;
import com.example.demo.graph.CustomWeightEdge;
import com.example.demo.repository.EdgeListRepo;

import lombok.extern.slf4j.Slf4j;
import org.jgrapht.DirectedGraph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.*;


@Component
@Slf4j
public class GraphSolver {

    DijkstraShortestPath<CustomNode, CustomWeightEdge> shortestPathAlgorithm;
    AllDirectedPaths<String, CustomWeightEdge> allPaths;
    DefaultDirectedWeightedGraph<CustomNode, CustomWeightEdge> graph;
    Map<String, String> edgeMap = new HashMap<>();
    ConnectivityInspector ci = null;
    HashSet<CustomNode> nodeSet = null;
    final EdgeListRepo edgeListRepo;
    public GraphSolver(EdgeListRepo edgeListRepo) {
        this.edgeListRepo = edgeListRepo;
    }

    protected void init() {
        Iterable<EdgeList> tempEdgeList = edgeListRepo.findAll();
        for (EdgeList edgeList1 : tempEdgeList) {
            edgeMap.putIfAbsent(edgeList1.getOrigin(), edgeList1.getDestCity());
            edgeMap.putIfAbsent(edgeList1.getDestination(), edgeList1.getDestCity());
        }
        graph = new DefaultDirectedWeightedGraph<>(CustomWeightEdge.class);
        for (EdgeList edge : tempEdgeList) {
            graph.addVertex(edge.getOriginNode());
            graph.addVertex(edge.getDestNode());
            CustomWeightEdge weightedEdge = graph.addEdge(edge.getOriginNode(), edge.getDestNode());
            weightedEdge.setWeight(edge.getTimes());
        }
        shortestPathAlgorithm = new DijkstraShortestPath<>(graph);
       // allPaths = new AllDirectedPaths<>(graph);
        ci = new ConnectivityInspector<>(graph);
    }

    public List<CustomNode> getAllNodes() {
        if (nodeSet == null) {
            nodeSet = new HashSet<>();
            for (Map.Entry<String, String> edge : this.edgeMap.entrySet()) {
                nodeSet.add(new CustomNode(edge.getKey(), edge.getValue()));
            }
        }
        return nodeSet.stream().toList();
    }

    @Cacheable("spaths")
    public List<CustomNode> shortestPathVertex(CustomNode origin, CustomNode destination) {
        GraphPath<CustomNode, CustomWeightEdge> shortestPath = shortestPathAlgorithm.getPath(origin, destination);
        if (shortestPath != null) {
            return shortestPath.getVertexList();
        } else {
            return Collections.emptyList();
        }
    }

    public List<List<String>> getAllPath(CustomNode origin, CustomNode destination) {
        List<GraphPath<String, CustomWeightEdge>> allPath =
                allPaths.getAllPaths(origin.getCode(), destination.getCode(), true, null);

        if (allPath != null) {
            List<List<String>> allPathLists = new ArrayList<>();
            for (GraphPath<String, CustomWeightEdge> path : allPath) {
                allPathLists.add(path.getVertexList());
            }
            return allPathLists;
        } else {
            return Collections.emptyList();
        }
    }

    public List<Set> connectedComponents() {
        return ci.connectedSets();
    }

    public String getValue(String key) {
        return edgeMap.get(key);
    }

}
