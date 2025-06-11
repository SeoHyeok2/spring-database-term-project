package com.admin.backend_answer.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long flightId;

    private String flightName;
    private String departureAirport;
    private String arrivalAirport;
    private Long fee;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;

    @Builder
    public Flight(
            String flightName,
            String departureAirport,
            String arrivalAirport,
            Long fee,
            LocalDateTime departureTime,
            LocalDateTime arrivalTime) {
        this.flightName = flightName;
        this.departureAirport = departureAirport;
        this.arrivalAirport = arrivalAirport;
        this.fee = fee;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
    }

}
