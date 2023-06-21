package com.example.demo.repository;


import com.example.demo.entity.Ontime;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OntimeRepo extends CrudRepository<Ontime, String> {
    @Query(value= "SELECT o.Origin as origin , o.Tail_Number as tailNumber,  count(*) AS hops, " +
            "arraySort(groupArray(o.DepTime)) as departures from ontime o " +
            "group by o.Origin, o.Tail_Number order by hops desc LIMIT 10", nativeQuery=true)
    List<ResultDao> getResult();


    @Query(value = "SELECT o.FlightDate as flightDate, o.Reporting_Airline as airline, o.Tail_Number as tailNumber,\n" +
            "    arraySort(groupArray(o.ArrTime)) AS arrivals, " +
            "    arraySort((x, y) -> y, groupArray(o.Origin), groupArray(o.ArrTime)) AS origins, " +
            "    arraySort((x, y) -> y, groupArray(o.Dest), groupArray(o.ArrTime)) AS destinations, " +
            "    arraySort((x, y) -> y, groupArray(o.DepTime), groupArray(o.ArrTime)) AS departures " +
            "  FROM ontime o" +
            "  WHERE o.DepTime < o.ArrTime AND tailNumber != ''\n" +
            "  GROUP BY flightDate, airline, tailNumber " +
            "  ORDER BY flightDate, airline, tailNumber LIMIT 2", nativeQuery = true)
    List<NewResultDao> getAnalysis();

}
