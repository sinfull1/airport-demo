package com.example.demo;

import com.example.demo.entity.EdgeList;
import com.example.demo.graph.CustomNode;
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
import org.jgrapht.graph.DefaultWeightedEdge;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.*;


@Component
@Slf4j
public class GraphSolver {
    StrongConnectivityAlgorithm<CustomNode, DefaultWeightedEdge> scAlg;

    DijkstraShortestPath<CustomNode, DefaultWeightedEdge> shortestPathAlgorithm;
    AllDirectedPaths<CustomNode, DefaultWeightedEdge> allPaths;
    DefaultDirectedWeightedGraph<CustomNode, DefaultWeightedEdge> graph;
    Map<String, String> cityCodeToNameMap = new HashMap<>();
    ConnectivityInspector<CustomNode, DefaultWeightedEdge> ci = null;
    HashSet<CustomNode> nodeSet = null;
    final EdgeListRepo edgeListRepo;

    public GraphSolver(EdgeListRepo edgeListRepo) {
        this.edgeListRepo = edgeListRepo;
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
        graph = new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);
        for (EdgeList edge : tempEdgeList) {
            graph.addVertex(edge.getOriginNode());
            graph.addVertex(edge.getDestNode());
            DefaultWeightedEdge weightedEdge = graph.addEdge(edge.getOriginNode(), edge.getDestNode());
            graph.setEdgeWeight(weightedEdge, edge.getTimes());
        }
        shortestPathAlgorithm = new DijkstraShortestPath<>(graph);
        allPaths = new AllDirectedPaths<>(graph);
        scAlg = new KosarajuStrongConnectivityInspector<>(graph);
    }

    public List<CustomNode> getAllNodes() {
        return nodeSet.stream().toList();
    }

    @Cacheable("spaths")
    public List<CustomNode> shortestPathVertex(CustomNode origin, CustomNode destination) {
        GraphPath<CustomNode, DefaultWeightedEdge> shortestPath = shortestPathAlgorithm.getPath(origin, destination);
        if (shortestPath != null) {
            return shortestPath.getVertexList();
        } else {
            return Collections.emptyList();
        }
    }
    public List<List<CustomNode>> getAllPath(CustomNode origin, CustomNode destination) {
        List<GraphPath<CustomNode, DefaultWeightedEdge>> allPath =
                allPaths.getAllPaths(origin, destination, true, null);

        if (allPath != null) {
            List<List<CustomNode>> allPathLists = new ArrayList<>();
            for (GraphPath<CustomNode, DefaultWeightedEdge> path : allPath) {
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

    public  List<Set<CustomNode>> connectComponents( ) {
        List<Set<CustomNode>> customNodes = new ArrayList<>();

        List<Graph<CustomNode, DefaultWeightedEdge>>  scs =  scAlg.getStronglyConnectedComponents();
        for (Graph<CustomNode, DefaultWeightedEdge> subGraph: scs) {
            customNodes.add(subGraph.vertexSet());
        }
        return customNodes;
    }
}
