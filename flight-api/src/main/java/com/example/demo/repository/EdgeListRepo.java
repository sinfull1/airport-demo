package com.example.demo.repository;

import com.example.demo.dao.EdgeResultDao;
import com.example.demo.dao.OriginList;
import com.example.demo.entity.EdgeList;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EdgeListRepo extends CrudRepository<EdgeList, String> {

    @Query(value = "select x.origin as origin , x.destination as destination, " +
            "  x.arrTime as arrTime, x.depTime as depTime, " +
            "x.airlines as airlines from  (select e.origin as origin, e.destination as destination, " +
            "(arrayJoin(arrayZip(e.arrTimes, e.depTimes, e.airline)) AS t).1 as arrTime, " +
            " t.2 as depTime, t.3 as airlines " +
            "from edges e ) x order by 1,2,5,3,4", nativeQuery = true)
    List<EdgeResultDao> getDestinationsAll();

    @Query(value = "select  e.origin as origin, count(*) as counts from EdgeList e group by e.origin order by 2 LIMIT 50")
    List<OriginList> getAllOrigin();

}
