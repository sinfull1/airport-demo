package com.example.demo;

import com.example.demo.repository.AirlineGuestRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

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