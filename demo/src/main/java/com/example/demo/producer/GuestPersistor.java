package com.example.demo.producer;

import com.example.demo.repository.AirlineGuestRepo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Function;


@Component
@Slf4j
public class GuestPersistor {

    final AirlineGuestRepo airlineGuestRepo;

    public GuestPersistor(AirlineGuestRepo airlineGuestRepo) {
        this.airlineGuestRepo = airlineGuestRepo;
    }

    @Bean
    public Function<byte[], Void> transactions() {
        return guest -> {
            log.info("Processing order: " + SerializationUtils.deserialize(guest));
            airlineGuestRepo.save(SerializationUtils.deserialize(guest));
            return null;
        };

    }
}
