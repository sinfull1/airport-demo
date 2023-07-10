package com.example.demo.repository;

public interface EdgeResultDao {

    String getDestination();

    Boolean getFound();
    Long getArrTime();
    Long getDepTime();
    String getAirlines();

    default String getString() {
        return getFound() + ": Fly to "+ this.getDestination() + " from " + this.getDepTime() + " to " + this.getArrTime() +  " on " + this.getAirlines();
    }

}
