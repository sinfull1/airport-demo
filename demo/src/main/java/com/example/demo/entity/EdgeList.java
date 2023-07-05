package com.example.demo.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Getter
@Setter
@Table(name = "edges")
@TableEngine(name = "MergeTree")
@PartitionKey(columns = {"guestType"})
public class EdgeList {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;
    private String source;
    private String destination;
    private Float distance;
    
}
