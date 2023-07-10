package com.example.demo.repository;

import com.example.demo.entity.EdgeList;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EdgeListRepo extends CrudRepository<EdgeList, String> {

    @Query(value = "select e from EdgeList e where e.origin = :origin and e.depTimes > :arrTimes ")
    Iterable<EdgeList> getAllDest(@Param("origin") String origin, @Param("arrTimes") Long arrTimes);


    @Query(value = "select x.destination as destination, min(x.arrTime) as arrTime, min(x.depTime) as depTime, " +
            "x.airlines as airlines from  (select e.destination as destination, " +
            "(arrayJoin(arrayZip(e.arrTimes, e.depTimes, e.airline)) AS t).1 as arrTime, " +
            " t.2 as depTime, t.3 as airlines from edges e  where origin = :origin ) x group by 1, 4", nativeQuery = true)
    List<EdgeResultDao> getNew(@Param("origin") String origin);

}
