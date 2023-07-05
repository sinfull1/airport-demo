package com.example.demo.repository;

import com.example.demo.entity.AirlineGuest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AirlineGuestRepo extends CrudRepository<AirlineGuest, String> {
}
