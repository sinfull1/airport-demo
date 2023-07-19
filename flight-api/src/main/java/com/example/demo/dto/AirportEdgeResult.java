package com.example.demo.dto;

import com.example.demo.dao.EdgeResultDao;
import com.example.demo.graph.CustomNode;
import com.redis.om.spring.annotations.Document;
import com.redis.om.spring.annotations.Indexed;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;

@Data
@RequiredArgsConstructor(staticName = "of")
@AllArgsConstructor
@Document
//@RedisHash
public class AirportEdgeResult implements Comparable<AirportEdgeResult> {
    // Id Field, also indexed
    @Id
    @Indexed
    private String id;
    @Indexed
    private String origin;
    @Indexed
    private String destination;

    @Indexed
    private Long arrTime;
    @Indexed
    private Long depTime;
    private String airline;

    public static AirportEdgeResult builder(EdgeResultDao edgeResultDao) {
        AirportEdgeResult airportEdgeResult = new AirportEdgeResult();
        airportEdgeResult.setOrigin(edgeResultDao.getOrigin());
        airportEdgeResult.setDestination(edgeResultDao.getDestination());
        airportEdgeResult.setAirline(edgeResultDao.getAirlines());
        airportEdgeResult.setArrTime(edgeResultDao.getArrTime());
        airportEdgeResult.setDepTime(edgeResultDao.getDepTime());
        return airportEdgeResult;
    }

    public CustomNode getOriginNode() {
        return new CustomNode(this.origin, this.arrTime);
    }

    public CustomNode getDestinationNode() {
        return new CustomNode(this.destination, this.arrTime);
    }

    @Override
    public int compareTo(@NotNull AirportEdgeResult o) {
        return this.getDepTime().compareTo(o.getDepTime());
    }
}
