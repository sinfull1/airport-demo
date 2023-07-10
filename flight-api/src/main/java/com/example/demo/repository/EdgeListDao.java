package com.example.demo.repository;

 public interface EdgeListDao {
     String getId();
     String getOrigin();
     String getOriginCity();
     String getDestination();
     String getDestCity();
     Object[] getAirline();
     Object[] getArrTimes();
     Object[] getDepTimes();
}
