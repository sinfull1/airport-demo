package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Getter
@Setter
@Table(name = "carrier")
@TableEngine(name = "MergeTree")
public class Carrier {

    @Id
    @Column(name = "code")
    private String code ;

    @Column(name = "airline")
    private String airline;




}
