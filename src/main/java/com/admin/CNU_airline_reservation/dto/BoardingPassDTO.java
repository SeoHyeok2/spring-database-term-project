package com.admin.CNU_airline_reservation.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

/**
 * 탑승권 이메일 발송에 필요한 데이터를 전달하기 위한 DTO (Data Transfer Object) 클래스
 * 여러 계층(Layer) 간에 데이터를 깔끔하게 전달하는 용도
 */
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