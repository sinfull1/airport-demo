package com.example.demo;

import io.micrometer.core.instrument.util.StringEscapeUtils;
import org.junit.Test;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("test")
public class OrderRepository {


    @Test
    public void test() {
        System.out.println(StringEscapeUtils.escapeJson("{\'sdfg\',34}"));
    }

}