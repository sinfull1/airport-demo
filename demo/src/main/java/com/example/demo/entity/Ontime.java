package com.example.demo.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.Date;

@Entity
@Getter
@Setter
@TableEngine(name = "MergeTree")
@Table(name = "ontime")
public class Ontime {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    @Column(name = "Year")
    private int year;

    @Column(name = "Quarter")
    private int quarter;

    @Column(name = "Month")
    private int month;

    @Column(name = "DayofMonth")
    private int dayOfMonth;

    @Column(name = "DayOfWeek")
    private int dayOfWeek;

    @Column(name = "FlightDate")
    private Date flightDate;

    @Column(name = "Reporting_Airline")
    private String reportingAirline;

    @Column(name = "DOT_ID_Reporting_Airline")
    private int dotIdReportingAirline;

    @Column(name = "IATA_CODE_Reporting_Airline")
    private String iataCodeReportingAirline;

    @Column(name = "Tail_Number")
    private String tailNumber;

    @Column(name = "Flight_Number_Reporting_Airline")
    private String flightNumberReportingAirline;

    @Column(name = "OriginAirportID")
    private int originAirportID;

    @Column(name = "OriginAirportSeqID")
    private int originAirportSeqID;

    @Column(name = "OriginCityMarketID")
    private int originCityMarketID;

    @Column(name = "Origin")
    private String origin;

    @Column(name = "OriginCityName")
    private String originCityName;

    @Column(name = "OriginState")
    private String originState;

    @Column(name = "OriginStateFips")
    private String originStateFips;

    @Column(name = "OriginStateName")
    private String originStateName;

    @Column(name = "OriginWac")
    private int originWac;

    @Column(name = "DestAirportID")
    private int destAirportID;

    @Column(name = "DestAirportSeqID")
    private int destAirportSeqID;

    @Column(name = "DestCityMarketID")
    private int destCityMarketID;

    @Column(name = "Dest")
    private String dest;

    @Column(name = "DestCityName")
    private String destCityName;

    @Column(name = "DestState")
    private String destState;

    @Column(name = "DestStateFips")
    private String destStateFips;

    @Column(name = "DestStateName")
    private String destStateName;

    @Column(name = "DestWac")
    private int destWac;

    @Column(name = "CRSDepTime")
    private int crsDepTime;

    @Column(name = "DepTime")
    private int depTime;

    @Column(name = "DepDelay")
    private String depDelay;

    @Column(name = "DepDelayMinutes")
    private String depDelayMinutes;

    @Column(name = "DepDel15")
    private String depDel15;

    @Column(name = "DepartureDelayGroups")
    private String departureDelayGroups;

    @Column(name = "DepTimeBlk")
    private String depTimeBlk;

    @Column(name = "TaxiOut")
    private String taxiOut;

    @Column(name = "WheelsOff")
    private String wheelsOff;

    @Column(name = "WheelsOn")
    private String wheelsOn;

    @Column(name = "TaxiIn")
    private String taxiIn;

    @Column(name = "CRSArrTime")
    private int crsArrTime;

    @Column(name = "ArrTime")
    private int arrTime;

    @Column(name = "ArrDelay")
    private String arrDelay;

    @Column(name = "ArrDelayMinutes")
    private String arrDelayMinutes;

    @Column(name = "ArrDel15")
    private String arrDel15;

    @Column(name = "ArrivalDelayGroups")
    private String arrivalDelayGroups;

    @Column(name = "ArrTimeBlk")
    private String arrTimeBlk;

    @Column(name = "Cancelled")
    private float cancelled;

    @Column(name = "CancellationCode")
    private String cancellationCode;

    @Column(name = "Diverted")
    private float diverted;

    @Column(name = "CRSElapsedTime")
    private float crsElapsedTime;

    @Column(name = "ActualElapsedTime")
    private String actualElapsedTime;

    @Column(name = "AirTime")
    private String airTime;

    @Column(name = "Flights")
    private String flights;

    @Column(name = "Distance")
    private float distance;

    @Column(name = "DistanceGroup")
    private int distanceGroup;

    @Column(name = "CarrierDelay", columnDefinition = "Nullable(Float32)" )
    @Nullable
    private Float carrierDelay;

    @Column(name = "WeatherDelay", columnDefinition = "Nullable(Float32)")
    private Float weatherDelay;

    @Column(name = "NASDelay", columnDefinition = "Nullable(Float32)")
    private Float nasDelay;

    @Column(name = "SecurityDelay", columnDefinition = "Nullable(Float32)")
    private Float securityDelay;

    @Column(name = "LateAircraftDelay",  columnDefinition = "Nullable(Float32)")
    private Float lateAircraftDelay;

    @Column(name = "FirstDepTime", columnDefinition = "Nullable(Float32)")
    private Float firstDepTime;

    @Column(name = "TotalAddGTime" , columnDefinition = "Nullable(Float32)")
    private Float totalAddGTime;

    @Column(name = "LongestAddGTime",  columnDefinition = "Nullable(Float32)")
    private Float longestAddGTime;

    @Column(name = "DivAirportLandings",  columnDefinition = "Nullable(Float32)")
    private Float divAirportLandings;

    @Column(name = "DivReachedDest",  columnDefinition = "Nullable(Float32)")
    private Float divReachedDest;

    @Column(name = "DivActualElapsedTime",  columnDefinition = "Nullable(Float32)")
    private Float divActualElapsedTime;

    @Column(name = "DivArrDelay",  columnDefinition = "Nullable(Float32)")
    private Float divArrDelay;

    @Column(name = "DivDistance",  columnDefinition = "Nullable(Float32)")
    private Float divDistance;

    @Column(name = "Div1Airport")
    private String div1Airport;

    @Column(name = "Div1AirportID",  columnDefinition = "Nullable(Int32)")
    private int div1AirportID;

    @Column(name = "Div1AirportSeqID",  columnDefinition = "Nullable(Int32)")
    private int div1AirportSeqID;

    @Column(name = "Div1WheelsOn",  columnDefinition = "Nullable(Int32)")
    private int div1WheelsOn;

    @Column(name = "Div1TotalGTime",  columnDefinition = "Nullable(Float32)")
    private Float div1TotalGTime;

    @Column(name = "Div1LongestGTime",  columnDefinition = "Nullable(Float32)")
    private Float div1LongestGTime;

    @Column(name = "Div1WheelsOff",  columnDefinition = "Nullable(Float32)")
    private Float div1WheelsOff;

    @Column(name = "Div1TailNum")
    private String div1TailNum;

    @Column(name = "Div2Airport")
    private String div2Airport;

    @Column(name = "Div2AirportID",  columnDefinition = "Nullable(Float32)")
    private Float div2AirportID;

    @Column(name = "Div2AirportSeqID",  columnDefinition = "Nullable(Float32)")
    private Float div2AirportSeqID;

    @Column(name = "Div2WheelsOn",  columnDefinition = "Nullable(Int32)")
    private int div2WheelsOn;

    @Column(name = "Div2TotalGTime",  columnDefinition = "Nullable(Float32)")
    private Float div2TotalGTime;

    @Column(name = "Div2LongestGTime",  columnDefinition = "Nullable(Float32)")
    private Float div2LongestGTime;

    @Column(name = "Div2WheelsOff",  columnDefinition = "Nullable(Float32)")
    private Float div2WheelsOff;

    @Column(name = "Div2TailNum")
    private String div2TailNum;

    @Column(name = "Div3Airport")
    private String div3Airport;

    @Column(name = "Div3AirportID",  columnDefinition = "Nullable(Int32)")
    private int div3AirportID;

    @Column(name = "Div3AirportSeqID",  columnDefinition = "Nullable(Int32)")
    private int div3AirportSeqID;

    @Column(name = "Div3WheelsOn",  columnDefinition = "Nullable(Int32)")
    private int div3WheelsOn;

    @Column(name = "Div3TotalGTime",  columnDefinition = "Nullable(Int32)")
    private int div3TotalGTime;

    @Column(name = "Div3LongestGTime",  columnDefinition = "Nullable(Int32)")
    private int div3LongestGTime;

    @Column(name = "Div3WheelsOff",  columnDefinition = "Nullable(Int32)")
    private int div3WheelsOff;

    @Column(name = "Div3TailNum")
    private String div3TailNum;

    @Column(name = "Div4Airport")
    private String div4Airport;

    @Column(name = "Div4AirportID",  columnDefinition = "Nullable(Int32)")
    private int div4AirportID;

    @Column(name = "Div4AirportSeqID",  columnDefinition = "Nullable(Int32)")
    private int div4AirportSeqID;

    @Column(name = "Div4WheelsOn",  columnDefinition = "Nullable(Int32)")
    private int div4WheelsOn;

    @Column(name = "Div4TotalGTime",  columnDefinition = "Nullable(Int32)")
    private int div4TotalGTime;

    @Column(name = "Div4LongestGTime",  columnDefinition = "Nullable(Int32)")
    private int div4LongestGTime;

    @Column(name = "Div4WheelsOff",  columnDefinition = "Nullable(Int32)")
    private int div4WheelsOff;

    @Column(name = "Div4TailNum")
    private String div4TailNum;

    @Column(name = "Div5Airport")
    private String div5Airport;

    @Column(name = "Div5AirportID",  columnDefinition = "Nullable(Int32)")
    private int div5AirportID;

    @Column(name = "Div5AirportSeqID",  columnDefinition = "Nullable(Int32)")
    private int div5AirportSeqID;

    @Column(name = "Div5WheelsOn", columnDefinition = "Nullable(Int32)")
    private int div5WheelsOn;

    @Column(name = "Div5TotalGTime", columnDefinition = "Nullable(Int32)")
    private int div5TotalGTime;

    @Column(name = "Div5LongestGTime", columnDefinition = "Nullable(Int32)")
    private int div5LongestGTime;

    @Column(name = "Div5WheelsOff", columnDefinition = "Nullable(Int32)")
    private int div5WheelsOff;

    @Column(name = "Div5TailNum")
    private String div5TailNum;

}
