package com.example.demo.repository;


import com.clickhouse.data.ClickHouseValue;
import com.example.demo.entity.Ontime;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.SortedSet;


public interface ResultDao {

    @Value("#{target.origin}")
    String getOrigin();

    @Value("#{target.tailNumber}")
    String getTailNumber();

    Integer getHops();

    Object[] getDepartures();

    Float getAverage();



}
