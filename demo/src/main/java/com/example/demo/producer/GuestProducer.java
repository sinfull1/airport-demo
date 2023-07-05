package com.example.demo.producer;

import com.example.demo.entity.AirlineGuest;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class GuestProducer {
    final StreamBridge streamBridge;

    public GuestProducer(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    @Scheduled(fixedDelay = 1000)
    public void sendBuyOrder() {
            streamBridge.send("orderBuySupplier-out-0",
                    SerializationUtils.serialize(AirlineGuest.getRandomGuest()));
    }
}
