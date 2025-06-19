package com.admin.CNU_airline_reservation.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// Airplane 엔티티
@Entity
@Table(name = "Airplane")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(AirplanePK.class)
public class Airplane {

    @Column(nullable = false)
    private String airline; // 항공사

    // --- 복합 기본 키(PK)를 구성하는 필드들 ---
    @Id
    @Column(length = 50)
    private String flightNo; // 운항편명 (복합 키 1)

    @Id
    private LocalDateTime departureDateTime; // 출발날짜 (복합 키 2)

    @Column(nullable = false)
    private String departureAirport; // 출발공항

    @Column(nullable = false)
    private LocalDateTime arrivalDateTime; // 도착날짜

    @Column(nullable = false)
    private String arrivalAirport; // 도착공항

    // 이 항공기에 속한 좌석 목록 (1:N 관계)
    @OneToMany(mappedBy = "airplane", cascade = CascadeType.ALL)
    private List<Seats> seats = new ArrayList<>();

    // 빌더 패턴을 위한 생성자
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
