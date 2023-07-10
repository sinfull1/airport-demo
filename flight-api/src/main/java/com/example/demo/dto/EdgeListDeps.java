package com.example.demo.dto;


import lombok.Data;

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
