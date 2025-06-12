package com.admin.CNU_airline_reservation.repository;

import com.admin.CNU_airline_reservation.entity.Airplane;
import com.admin.CNU_airline_reservation.entity.Seats;
import com.admin.CNU_airline_reservation.entity.SeatsPK; // SeatsId를 임포트
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
// 1. 엔티티는 Seats, ID 타입은 복합 키 클래스인 SeatsId로 지정합니다.
public interface SeatsRepository extends JpaRepository<Seats, SeatsPK> {

    /**
     * 특정 항공편(Airplane 엔티티)에 속한 모든 좌석 등급 목록을 조회합니다.
     * @param airplane 조회할 항공편 엔티티 객체
     * @return 좌석 등급 리스트
     */
    List<Seats> findByAirplane(Airplane airplane);

    /**
     * 항공편 번호와 출발 시간을 기준으로 특정 항공편에 속한 모든 좌석 등급 목록을 조회합니다.
     * 필드명에 _ (언더스코어)를 사용하여 연관된 엔티티의 필드에 접근할 수 있습니다.
     * @param flightNo 항공편 번호
     * @param departureDateTime 출발 시간
     * @return 좌석 등급 리스트
     */
    List<Seats> findByAirplane_FlightNoAndAirplane_DepartureDateTime(String flightNo, LocalDateTime departureDateTime);

    // save(seats), deleteById(seatsId), findById(seatsId) 등은 JpaRepository가 기본 제공합니다.
}