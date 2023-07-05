package com.example.demo;

import com.example.demo.repository.AirlineGuestRepo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@RestController
@Slf4j
public class OrderController {

    final StreamBridge streamBridge;
    final AirlineGuestRepo airlineGuestRepo;

    public OrderController(StreamBridge streamBridge, AirlineGuestRepo airlineGuestRepo) {
        this.streamBridge = streamBridge;
        this.airlineGuestRepo = airlineGuestRepo;
    }

    @PostMapping("/event")
    public Mono<Void> buy() {
        return Mono.empty();
    }





}