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
            " arrayReduce('groupUniqArray', groupArray(o.iataCodeReportingAirline)) as airline, " +
            " arrayAvg(groupArray(o.arrTime - o.depTime)) as times " +
            "  FROM Ontime o" +
            "  WHERE o.depTime < o.arrTime " +
            "  GROUP BY origin, origCity, destination, destCity " +
            "  ORDER BY origin, origCity, destination, destCity")
    List<EdgeListDao> getEdgeList();

    @Query(value =

            " SELECT o.origin as origin, " +
            " o.originCityName as origCity, " +
            " o.dest as destination, " +
            " o.destCityName as destCity, " +
            " arraySort(groupArray(toUInt64(toDateTime(o.flightDate, 'UTC')) + toUInt64(o.arrTime/100)*60*60 + (modulo(o.arrTime,100)*60)))  as arrTimes, " +
            " arraySort('(x, y) -> y',groupArray(o.iataCodeReportingAirline), groupArray(toUInt64(toDateTime(o.flightDate, 'UTC')) + toUInt64(o.arrTime/100)*60*60 + (modulo(o.arrTime,100)*60))) as airline, " +
            " arraySort('(x, y) -> y',groupArray(toUInt64(toDateTime(o.flightDate, 'UTC')) + toUInt64(o.depTime/100)*60*60 + (modulo(o.depTime,100)*60)), groupArray(toUInt64(toDateTime(o.flightDate, 'UTC')) + toUInt64(o.arrTime/100)*60*60 + (modulo(o.arrTime,100)*60))) as depTimes " +
            "  FROM Ontime o" +
            "  WHERE o.depTime < o.arrTime " +
            "  group BY 1, 2, 3, 4 " +
            "  ORDER BY 1, 3, 6, 7")
    List<EdgeListDao> getEdgeListWithDeps();
}
