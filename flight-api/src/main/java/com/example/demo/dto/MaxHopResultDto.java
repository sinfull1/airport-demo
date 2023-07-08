package com.example.demo.dto;

import com.example.demo.graph.CustomNode;
import lombok.Data;

import java.util.List;


@Data
public class MaxHopResultDto {

    private CustomNode source;
    private CustomNode destination;
    private List<String> airlines;
    private double flightTime;

}
