package com.admin.CNU_airline_reservation.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Airplane")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(AirplanePK.class)
public class Airplane {

    @Column(nullable = false)
    private String airline;

    @Id
    @Column(length = 50)
    private String flightNo;

    @Id
    private LocalDateTime departureDateTime;

    @Column(nullable = false)
    private String departureAirport;

    @Column(nullable = false)
    private LocalDateTime arrivalDateTime;

    @Column(nullable = false)
    private String arrivalAirport;

    @OneToMany(mappedBy = "airplane", cascade = CascadeType.ALL)
    private List<Seats> seats = new ArrayList<>();

    @Builder
    public Airplane(String flightNo, LocalDateTime departureDateTime, String airline, String departureAirport, LocalDateTime arrivalDateTime, String arrivalAirport) {
        this.flightNo = flightNo;
        this.departureDateTime = departureDateTime;
        this.airline = airline;
        this.departureAirport = departureAirport;
        this.arrivalDateTime = arrivalDateTime;
        this.arrivalAirport = arrivalAirport;
    }
}
