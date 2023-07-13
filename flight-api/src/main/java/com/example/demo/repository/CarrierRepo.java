package com.example.demo.repository;

import com.example.demo.entity.Carrier;
import org.springframework.data.repository.CrudRepository;

public interface CarrierRepo extends CrudRepository<Carrier, String> {
}
