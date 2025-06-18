package com.admin.CNU_airline_reservation.dto;

public interface ReservationStats {
    String get운항편명(); // SQL 쿼리의 AS 뒤에 오는 별칭과 메소드 이름이 일치해야 함
    String get좌석등급();
    int get예약수();
    long get지불금액();
}