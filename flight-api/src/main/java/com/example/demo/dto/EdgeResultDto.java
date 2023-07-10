package com.example.demo.dto;


import com.example.demo.repository.EdgeResultDao;
import lombok.Data;

@Data
public class EdgeResultDto {
    private String destination;
    long arrTime;
    long depTime;
    private String airline;

    public EdgeResultDto(EdgeResultDao edgeResultDao) {
        this.destination = edgeResultDao.getDestination();
        this.airline = edgeResultDao.getAirlines();
        this.arrTime = edgeResultDao.getArrTime();
        this.depTime = edgeResultDao.getDepTime();
    }

}
