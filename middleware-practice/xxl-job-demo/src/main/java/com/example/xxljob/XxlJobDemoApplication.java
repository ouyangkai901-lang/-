package com.example.xxljob;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class XxlJobDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(XxlJobDemoApplication.class, args);
    }
}
