package com.example.demo.repository;

import com.example.demo.entity.Reservation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReservationRepo extends CrudRepository<Reservation, UUID> {


}
