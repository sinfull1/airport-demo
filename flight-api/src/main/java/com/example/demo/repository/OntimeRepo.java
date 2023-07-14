package com.example.demo.repository;


import com.example.demo.dao.EdgeListDao;
import com.example.demo.entity.Ontime;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OntimeRepo extends CrudRepository<Ontime, String> {

//TODO: change it to one step conversion and use redis table engine to insert data
    @Query(value =
            "  SELECT o.origin as origin, " +
                    "  o.originCityName as originCity, " +
                    "  o.dest as destination, " +
                    "  o.destCityName as destCity, " +
                    "  arraySort(groupArray(toUInt64(toDateTime(o.flightDate, 'UTC')) + toUInt64(o.arrTime/100)*60*60 + (modulo(o.arrTime,100)*60)))  as arrTimes, " +
                    "  arraySort('(x, y) -> y',groupArray(o.iataCodeReportingAirline), groupArray(toUInt64(toDateTime(o.flightDate, 'UTC')) + toUInt64(o.arrTime/100)*60*60 + (modulo(o.arrTime,100)*60))) as airline, " +
                    "  arraySort('(x, y) -> y',groupArray(toUInt64(toDateTime(o.flightDate, 'UTC')) + toUInt64(o.depTime/100)*60*60 + (modulo(o.depTime,100)*60)), groupArray(toUInt64(toDateTime(o.flightDate, 'UTC')) + toUInt64(o.arrTime/100)*60*60 + (modulo(o.arrTime,100)*60))) as depTimes " +
                    "  FROM Ontime o" +
                    "  WHERE o.depTime < o.arrTime " +
                    "  AND o.flightDate > toDateTime('2023-01-03', 'UTC')  " +
                    "  AND o.flightDate < toDateTime('2023-01-12', 'UTC')  " +
                    "  group BY 1, 2, 3, 4 " +
                    "  ORDER BY 1, 3, 6, 7 ")
    List<EdgeListDao> getEdgeListWithDeps();
}
