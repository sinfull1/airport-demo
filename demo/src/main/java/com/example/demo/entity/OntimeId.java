package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;


@Data
@AllArgsConstructor
public class OntimeId implements Serializable {
    private int year;
    private int quarter;

    private int month;
    private int dayOfMonth;

    private int flightDate;
    private String iataCodeReportingAirline;

}
