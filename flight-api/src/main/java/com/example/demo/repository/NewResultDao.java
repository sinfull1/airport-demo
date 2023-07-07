package com.example.demo.repository;


public interface NewResultDao {

    String getFlightDate();

    String getAirline();

    String getTailNumber();

    Object[] getArrivals();

    Object[] getOrigins();

    Object[] getGroups();

    Object[] getTops();
}
