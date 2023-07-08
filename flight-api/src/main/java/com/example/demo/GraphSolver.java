package com.example.demo;

import com.example.demo.entity.Carrier;
import com.example.demo.entity.EdgeList;
import com.example.demo.graph.CustomNode;
import com.example.demo.graph.CustomWeightEdge;
import com.example.demo.repository.CarrierRepo;
import com.example.demo.repository.EdgeListRepo;
import lombok.extern.slf4j.Slf4j;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.connectivity.KosarajuStrongConnectivityInspector;
import org.jgrapht.alg.interfaces.StrongConnectivityAlgorithm;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.*;


@Component
@Slf4j
public class GraphSolver {
    StrongConnectivityAlgorithm<CustomNode, CustomWeightEdge> scAlg;

    DijkstraShortestPath<CustomNode, CustomWeightEdge> shortestPathAlgorithm;
    AllDirectedPaths<CustomNode, CustomWeightEdge> allPaths;
    DefaultDirectedWeightedGraph<CustomNode, CustomWeightEdge> graph;
    Map<String, String> cityCodeToNameMap = new HashMap<>();
    ConnectivityInspector<CustomNode, CustomWeightEdge> ci = null;
    HashSet<CustomNode> nodeSet = null;
    final EdgeListRepo edgeListRepo;



    static final Map<String, String> codeToAirline = new HashMap<>();

    final CarrierRepo carrierRepo;

    public GraphSolver(EdgeListRepo edgeListRepo, CarrierRepo carrierRepo) {
        this.edgeListRepo = edgeListRepo;
        this.carrierRepo = carrierRepo;
    }

    protected void init() {
        Iterable<EdgeList> tempEdgeList = edgeListRepo.findAll();
        nodeSet = new HashSet<>();
        for (EdgeList edgeList1 : tempEdgeList) {
            cityCodeToNameMap.putIfAbsent(edgeList1.getOrigin(), edgeList1.getDestCity());
            cityCodeToNameMap.putIfAbsent(edgeList1.getDestination(), edgeList1.getDestCity());
            nodeSet.add(new CustomNode(edgeList1.getOrigin(), edgeList1.getOriginCity()));
            nodeSet.add(new CustomNode(edgeList1.getDestination(), edgeList1.getDestCity()));
        }
        graph = new DefaultDirectedWeightedGraph<>(CustomWeightEdge.class);
        for (EdgeList edge : tempEdgeList) {
            if ( edge.getTimes() != null) {
                graph.addVertex(edge.getOriginNode());
                graph.addVertex(edge.getDestNode());
                CustomWeightEdge weightedEdge = graph.addEdge(edge.getOriginNode(), edge.getDestNode());
                weightedEdge.setAirline(edge.getAirline());
                weightedEdge.setWeight(edge.getTimes());
                if (weightedEdge != null ) {
                    graph.setEdgeWeight(weightedEdge, edge.getTimes());
                }
            }
        }
        for (Carrier carrier : carrierRepo.findAll()) {
            codeToAirline.put(carrier.getCode(), carrier.getAirline());
        }
        shortestPathAlgorithm = new DijkstraShortestPath<>(graph);
        allPaths = new AllDirectedPaths<>(graph);
        scAlg = new KosarajuStrongConnectivityInspector<>(graph);
    }

    public List<CustomNode> getAllNodes() {
        return nodeSet.stream().toList();
    }

    @Cacheable("spaths")
    public GraphPath<CustomNode, CustomWeightEdge> shortestPathVertex(CustomNode origin, CustomNode destination) {
        GraphPath<CustomNode, CustomWeightEdge> shortestPath = shortestPathAlgorithm.getPath(origin, destination);
        if (shortestPath != null) {
            return shortestPath;
        } else {
            return null;
        }
    }
    public List<List<CustomNode>> getAllPath(CustomNode origin, CustomNode destination) {
        List<GraphPath<CustomNode, CustomWeightEdge>> allPath =
                allPaths.getAllPaths(origin, destination, true, null);

        if (allPath != null) {
            List<List<CustomNode>> allPathLists = new ArrayList<>();
            for (GraphPath<CustomNode, CustomWeightEdge> path : allPath) {
                allPathLists.add(path.getVertexList());
            }
            return allPathLists;
        } else {
            return Collections.emptyList();
        }
    }
    public String getValue(String key) {
        return cityCodeToNameMap.get(key);
    }

    public static String getCodeToAirline(String key) {
        return codeToAirline.get(key);
    }
    public  List<Set<CustomNode>> connectComponents( ) {
        List<Set<CustomNode>> customNodes = new ArrayList<>();

        List<Graph<CustomNode, CustomWeightEdge>>  scs =  scAlg.getStronglyConnectedComponents();
        for (Graph<CustomNode, CustomWeightEdge> subGraph: scs) {
            customNodes.add(subGraph.vertexSet());
        }
        return customNodes;
    }
}
