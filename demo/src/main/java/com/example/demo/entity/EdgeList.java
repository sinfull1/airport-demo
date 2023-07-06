package com.example.demo.entity;


import com.example.demo.repository.EdgeListDao;
import io.hypersistence.utils.hibernate.type.array.ListArrayType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "edges")
@TableEngine(name = "MergeTree")
public class EdgeList  implements EdgeListDao {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    @Column(name="origin")
    private String origin;
    @Column(name="destination")
    private String destination;
    @Column(name = "times")
    private Float times;
    
}
