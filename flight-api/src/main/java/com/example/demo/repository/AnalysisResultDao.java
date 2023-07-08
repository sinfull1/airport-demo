package com.example.demo.repository;


public interface AnalysisResultDao {

    String getFlightDate();

    String getAirline();

    String getTailNumber();

    Object[] getArrivals();

    Object[] getDepartures();

    Object[] getOrigins();

    Object[] getDests();

    Float getHops();
}
