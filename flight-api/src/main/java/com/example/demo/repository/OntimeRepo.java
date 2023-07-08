package com.example.demo.repository;


import com.example.demo.entity.Ontime;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OntimeRepo extends CrudRepository<Ontime, String> {


    @Query(value =

            " SELECT o.flightDate as flightDate, o.reportingAirline as airline, o.tailNumber as tailNumber, " +
                    "    arraySort(groupArray(o.arrTime)) AS arrivals, " +
                    "    arraySort(groupArray(o.depTime)) AS departures, " +
                    "    arraySort('(x, y) -> y', groupArray(o.origin), groupArray(o.arrTime)) AS origins, " +
                    "    arraySort('(x, y) -> y', groupArray(o.dest), groupArray(o.arrTime)) AS dests, " +
                    "    length(groupArray(o.depTime)) AS hops " +
                    "  FROM Ontime o" +
                    "  WHERE o.depTime < o.arrTime " +
                    "  GROUP BY flightDate, airline, tailNumber " +
                    "  ORDER BY hops DESC, airline, tailNumber LIMIT 10 "

    )
    List<AnalysisResultDao> getAnalysis();

    @Query(value = "SELECT o.origin as origin, " +
            " o.originCityName as origCity, " +
            " o.dest as destination, " +
            " o.destCityName as destCity, " +
            "  arrayAvg(groupArray(o.arrTime - o.depTime)) as times " +
            "  FROM Ontime o" +
            "  WHERE o.depTime < o.arrTime " +
            "  GROUP BY origin, origCity, destination, destCity " +
            "  ORDER BY origin, origCity, destination, destCity")
    List<EdgeListDao> getEdgeList();
}
