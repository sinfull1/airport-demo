package com.example.demo;

import com.example.demo.entity.Carrier;
import com.example.demo.entity.EdgeList;
import com.example.demo.graph.CustomNode;
import com.example.demo.graph.CustomWeightEdge;
import com.example.demo.repository.CarrierRepo;
import com.example.demo.repository.EdgeListDao;
import com.example.demo.repository.EdgeListRepo;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.mapping.ClickHouseArrayMapper;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.connectivity.KosarajuStrongConnectivityInspector;
import org.jgrapht.alg.interfaces.StrongConnectivityAlgorithm;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.alg.shortestpath.BellmanFordShortestPath;

import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.*;


@Component
@Slf4j
public class GraphSolver {
    StrongConnectivityAlgorithm<CustomNode, CustomWeightEdge> scAlg;
    TopologicalOrderIterator<CustomNode, CustomWeightEdge> moreDependencyFirstIterator;
    BellmanFordShortestPath<CustomNode, CustomWeightEdge> shortestPathAlgorithm;
    AllDirectedPaths<CustomNode, CustomWeightEdge> allPaths;
    DirectedWeightedMultigraph<CustomNode, CustomWeightEdge> graph;
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

    protected void init(List<EdgeListDao> edgeLists) {

        graph = new DirectedWeightedMultigraph<>(CustomWeightEdge.class);
        nodeSet = new HashSet<>();
        for (EdgeListDao edge : edgeLists) {
            cityCodeToNameMap.putIfAbsent(edge.getOrigin(), edge.getDestCity());
            cityCodeToNameMap.putIfAbsent(edge.getDestination(), edge.getDestCity());
            nodeSet.add(new CustomNode(edge.getOrigin(), edge.getOrigCity()));
            nodeSet.add(new CustomNode(edge.getDestination(), edge.getDestCity()));
        }
        for (EdgeListDao edge : edgeLists) {
                graph.addVertex(edge.getOriginNode());
                graph.addVertex(edge.getDestNode());
                List<String> airlines = ClickHouseArrayMapper.getOrderedStringSet(edge.getAirline());
                List<Integer> arrTimes = ClickHouseArrayMapper.getOrderedIntegerSet(edge.getArrTimes());
                List<Integer> depTimes = ClickHouseArrayMapper.getOrderedIntegerSet(edge.getDepTimes());
                for (int i =0; i< airlines.size(); i++) {
                    CustomWeightEdge weightedEdge = graph.addEdge(edge.getOriginNode(), edge.getDestNode());
                    weightedEdge.setAirline(airlines.get(i));
                    weightedEdge.setStartTime(depTimes.get(i));
                    weightedEdge.setEndTime(arrTimes.get(i));
                    graph.setEdgeWeight(weightedEdge, arrTimes.get(i) - depTimes.get(i));
                }
        }
        for (Carrier carrier : carrierRepo.findAll()) {
            codeToAirline.put(carrier.getCode(), carrier.getAirline());
        }
        shortestPathAlgorithm = new BellmanFordShortestPath<>(graph);
        allPaths = new AllDirectedPaths<>(graph);
        scAlg = new KosarajuStrongConnectivityInspector<>(graph);
        moreDependencyFirstIterator = new TopologicalOrderIterator<>(graph);
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


    public void getTopologyOrder() {
        moreDependencyFirstIterator.forEachRemaining(x->System.out.print(x.getCity()));
    }
}
