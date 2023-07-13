package com.example.demo.dao;

public interface EdgeResultDao {

    String getOrigin();

    String getDestination();

    Long getArrTime();

    Long getDepTime();

    String getAirlines();

    default String getString() {
        return getOrigin() + ": Fly to " + this.getDestination() + " from " + this.getDepTime() + " to " + this.getArrTime() + " on " + this.getAirlines();
    }

}
