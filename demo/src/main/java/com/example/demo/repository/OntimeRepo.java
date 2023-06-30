package com.example.demo.repository;


import com.clickhouse.client.ClickHouseResponse;
import com.example.demo.entity.Ontime;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.function.Function;

@Repository
public interface OntimeRepo extends CrudRepository<Ontime, String> {


    @Query(value = "SELECT o.flightDate as flightDate, o.reportingAirline as airline, o.tailNumber as tailNumber,\n" +
            "    arraySort(groupArray(o.arrTime)) AS arrivals, " +
            "    arraySort('(x, y) -> y', groupArray(o.origin), groupArray(o.arrTime)) AS origins, " +
            "    arraySort('(x, y) -> y', groupArray(o.dest), groupArray(o.arrTime)) AS destinations, " +
            "    arraySort('(x, y) -> y', groupArray(o.depTime), groupArray(o.arrTime)) AS departures " +
            "  FROM Ontime o" +
            "  WHERE o.depTime < o.arrTime " +
            "  GROUP BY flightDate, airline, tailNumber " +
            "  ORDER BY flightDate, airline, tailNumber LIMIT 2")
    List<NewResultDao> getAnalysis();




}
