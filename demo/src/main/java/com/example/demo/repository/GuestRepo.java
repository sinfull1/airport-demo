package com.example.demo.repository;

import com.example.demo.entity.Guest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuestRepo extends CrudRepository<Guest, String> {


}
