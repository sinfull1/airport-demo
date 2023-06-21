package com.example.demo;

import com.example.demo.entity.Order;
import org.apache.kafka.clients.producer.internals.DefaultPartitioner;
import org.apache.kafka.common.Cluster;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class CustomPartitioner extends DefaultPartitioner {

    @Override
    public int partition(String topic, Object key, byte[] keyBytes,
                         Object value, byte[] valueBytes,
                         Cluster cluster) {

        String partitionKey = null;
        if (Objects.nonNull(key)) {
            Order bookingKey = (Order) key;
            keyBytes = bookingKey.getFirstName().getBytes(StandardCharsets.UTF_8);
        }
        return super.partition(topic, partitionKey, keyBytes, value,
                valueBytes, cluster);
    }
}
