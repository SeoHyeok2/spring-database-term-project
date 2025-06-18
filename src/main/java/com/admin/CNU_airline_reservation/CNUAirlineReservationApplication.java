package com.admin.CNU_airline_reservation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class CNUAirlineReservationApplication {

    public static void main(String[] args) {
        SpringApplication.run(CNUAirlineReservationApplication.class, args);
    }

}
