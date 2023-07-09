package com.example.demo.dto;


import com.example.demo.graph.CustomNode;
import com.example.demo.repository.EdgeListDao;
import io.hypersistence.utils.hibernate.type.array.ListArrayType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.mapping.ClickHouseArrayMapper;

import java.util.List;

@Data
public class EdgeListDeps {
    private String origin;
    private String originCity;

    private String destination;

    private String destCity;

    private List<String> airline;
    private List<Long> arrTimes;
    private List<Long> depTimes;



}
