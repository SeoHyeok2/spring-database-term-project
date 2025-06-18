package com.admin.CNU_airline_reservation.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder // 빌더 패턴을 사용하기 위해 추가
public class BoardingPassDTO {
    private String customerName;
    private String customerEmail;
    private LocalDateTime reservationTime;
    private String airline;
    private String flightNo;
    private String seatClass;
    private String departureAirport;
    private LocalDateTime departureDateTime;
    private String arrivalAirport;
    private LocalDateTime arrivalDateTime;
}