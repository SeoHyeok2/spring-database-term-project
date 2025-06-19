package com.admin.CNU_airline_reservation.dto;

/**
 * 예약 통계 데이터를 담기 위한 인터페이스 기반 프로젝션(Projection)
 * Spring Data JPA Repository에서 특정 컬럼만 조회한 결과를 매핑하는 용도
 * 구현 클래스를 직접 만들 필요 없이, JPA가 자동으로 프록시(Proxy) 객체를 생성하여 데이터를 전달
 */
public interface ReservationStats {
    String get운항편명();
    String get좌석등급();
    int get예약수();
    long get지불금액();
}