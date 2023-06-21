package com.example.demo;


import lombok.extern.slf4j.Slf4j;
import org.hibernate.boot.model.naming.ImplicitNamingStrategy;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyLegacyJpaImpl;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.r2dbc.ConnectionFactoryHealthContributorAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.r2dbc.R2dbcDataAutoConfiguration;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Random;
import java.util.function.Function;

@SpringBootApplication(exclude = {ConnectionFactoryHealthContributorAutoConfiguration.class,
        R2dbcAutoConfiguration.class,
        R2dbcDataAutoConfiguration.class})

@Slf4j
@EnableScheduling
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
    @Bean
    public PhysicalNamingStrategy physical() {
        return new PhysicalNamingStrategyStandardImpl();
    }

    @Bean
    public ImplicitNamingStrategy implicit() {
        return new ImplicitNamingStrategyLegacyJpaImpl();
    }

    @Autowired
    private StreamBridge streamBridge;

    /*
	@Scheduled(fixedDelay = 1000)
	public void sendBuyOrder() {
		for ( int i =0; i<1;i++) {
			Order order = generateBuyOrder();
			log.info("Sending buy order: " + order);
			streamBridge.send("orderBuySupplier-out-0", order);
		}
	}

	@Scheduled(fixedDelay = 1000)
	public void sendSellOrder() {
		for ( int i =0; i<1;i++) {
			Order order = generateSellOrder();
			log.info("Sending sell order: " + order);
			streamBridge.send("orderSellSupplier-out-0", order);
		}
	}
*/

    private long transactionId;
    private static Long orderId = 0l;
    private static final Random r = new Random();
    private static final Integer[] products = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

    public static Object generateBuyOrder() {
        return null;
    }


    // write a bean that returns a BiFunction that takes two KStreams as input and returns a KStream as output


    @Bean
    public Function<String, Void> transactions() {
        return order -> {
            log.info("Processing order: " + order);
            System.out.println("Processing order: " + order);
            //       orderRepository.add(order).block();
            return null;
        };

    }


}