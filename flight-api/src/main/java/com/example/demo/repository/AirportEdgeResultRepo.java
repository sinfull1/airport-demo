package com.example.demo.repository;

import com.example.demo.dto.AirportEdgeResult;
import com.redis.om.spring.repository.RedisDocumentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AirportEdgeResultRepo extends RedisDocumentRepository<AirportEdgeResult, String> {
    Page<AirportEdgeResult> findByOriginAndDepTimeBetween(String origin, Long minValue, Long maxValue, Pageable pageRequest);

}