package com.admin.CNU_airline_reservation.dto;

public interface CancellationStats {
    String get운항편명();
    String get회원번호();
    String get좌석등급();
    int get환불금액();
    long get취소순위();
}