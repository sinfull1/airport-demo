package com.example.demo.dto;

import lombok.Data;

@Data
public class Path {
    String origin;
    String destination;
    Integer arrival;

    Integer departure;
}
