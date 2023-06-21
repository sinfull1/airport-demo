package com.example.demo.repository;


import com.clickhouse.data.ClickHouseValue;
import org.springframework.beans.factory.annotation.Value;

import java.util.SortedSet;


public interface NewResultDao {

    String getFlightDate();

    String getAirline();

    String getTailNumber();

    Object[] getArrivals();
    Object[] getOrigins();
    Object[] getDestinations();
    Object[] getDepartures ();

}
