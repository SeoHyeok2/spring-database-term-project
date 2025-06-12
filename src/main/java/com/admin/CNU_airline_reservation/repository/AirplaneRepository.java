package com.admin.CNU_airline_reservation.repository;

import com.admin.CNU_airline_reservation.entity.Airplane;
import com.admin.CNU_airline_reservation.entity.AirplanePK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AirplaneRepository extends JpaRepository<Airplane, AirplanePK> {

    /**
     * 출발지, 도착지, 특정 출발 날짜로 항공편과 관련 좌석 정보를 함께 조회합니다.
     */
    // 1. JOIN FETCH a.seats 구문을 추가하여 N+1 문제를 해결합니다.
    @Query("SELECT a FROM Airplane a JOIN FETCH a.seats s " +
            "WHERE a.departureAirport = :departureAirport " +
            "AND a.arrivalAirport = :arrivalAirport " +
            "AND a.departureDateTime BETWEEN :startOfDay AND :endOfDay")
    List<Airplane> findAirplanesWithSeatsByConditions(
            @Param("departureAirport") String departureAirport,
            @Param("arrivalAirport") String arrivalAirport,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );

    /**
     * 모든 항공편과 관련 좌석 정보를 출발 시간 오름차순으로 정렬하여 조회합니다.
     */
    // 2. 이 메소드에도 JOIN FETCH를 적용합니다.
    @Query("SELECT a FROM Airplane a JOIN FETCH a.seats ORDER BY a.departureDateTime ASC")
    List<Airplane> findAllWithSeatsOrderByDepartureDateTimeAsc();
}