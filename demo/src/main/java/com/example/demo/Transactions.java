package com.example.demo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transactions {


    private long id;
    private long orderBuyId;
    private long orderSellId;
    private long count;
    private float amount;
    long time;
    String status;


}
