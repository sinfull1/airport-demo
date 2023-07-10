package com.example.demo.repository;

import com.example.demo.entity.EdgeList;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EdgeListRepo extends CrudRepository<EdgeList, String> {

    @Query(value = " select e.origin, e.originCity, e.destination, e.destCity, e.airline,e.arrTimes,e.depTimes from EdgeList e where e.origin = :origin ")
    List<EdgeListDao> getAllDest(@Param("origin") String origin);
}
