package com.example.demo.repository;

public interface EdgeListDao {
    String getOrigin();

    String getOrigCity();

    String getDestination();

    String getDestCity();

    Object[] getAirline();

    Float getTimes();

}
