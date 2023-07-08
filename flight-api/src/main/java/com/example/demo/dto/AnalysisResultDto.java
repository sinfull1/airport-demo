package com.example.demo.dto;

import lombok.Data;

import java.util.List;

@Data
public class AnalysisResultDto {
    String flightDate;
    String tailNumber;
    String airline;
    List<Path> paths;
    Integer hops;

}
