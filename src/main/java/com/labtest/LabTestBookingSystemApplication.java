package com.labtest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class LabTestBookingSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(LabTestBookingSystemApplication.class, args);
    }
}
