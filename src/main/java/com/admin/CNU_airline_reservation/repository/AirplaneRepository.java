package com.admin.CNU_airline_reservation.repository;

import com.admin.CNU_airline_reservation.entity.Airplane;
import com.admin.CNU_airline_reservation.entity.AirplanePK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AirplaneRepository extends JpaRepository<Airplane, AirplanePK> {

    /**
     * 출발지, 도착지, 특정 출발 날짜로 항공편과 관련 좌석 정보를 함께 조회합니다.
     */
    @Query("SELECT DISTINCT a FROM Airplane a JOIN FETCH a.seats s " +
            "WHERE a.departureAirport = :departureAirport " +
            "AND a.arrivalAirport = :arrivalAirport " +
            "AND FUNCTION('DATE', a.departureDateTime) = :departureDate " +
            "ORDER BY s.price ASC")
    List<Airplane> findAirplanesByDateOrderByPriceAsc(
            @Param("departureAirport") String departureAirport,
            @Param("arrivalAirport") String arrivalAirport,
            @Param("departureDate") LocalDate departureDate
    );

    /**
     * 출발지, 도착지, 특정 출발 날짜로 항공편과 관련 좌석 정보를 함께 조회합니다.
     */
    @Query("SELECT DISTINCT a FROM Airplane a " +
            "WHERE a.departureAirport = :departureAirport " +
            "AND a.arrivalAirport = :arrivalAirport " +
            "AND FUNCTION('DATE', a.departureDateTime) = :departureDate " +
            "ORDER BY a.departureDateTime ASC")
    List<Airplane> findAirplanesByDateOrderByDepartureDateTimeAsc(
            @Param("departureAirport") String departureAirport,
            @Param("arrivalAirport") String arrivalAirport,
            @Param("departureDate") LocalDate departureDate
    );

    /**
     * 출발지, 도착지, 특정 출발 날짜로 항공편과 관련 좌석 정보를 함께 조회합니다.
     */
    @Query("SELECT DISTINCT a FROM Airplane a JOIN FETCH a.seats s " +
            "WHERE a.departureAirport = :departureAirport " +
            "AND a.arrivalAirport = :arrivalAirport " +
            "AND FUNCTION('DATE', a.departureDateTime) = :departureDate " +
            "ORDER BY s.no_of_seats DESC")
    List<Airplane> findAirplanesByDateOrderByNoOfSeatsDesc(
            @Param("departureAirport") String departureAirport,
            @Param("arrivalAirport") String arrivalAirport,
            @Param("departureDate") LocalDate departureDate
    );

    /**
     * 모든 항공편과 관련 좌석 정보를 '좌석 요금' 오름차순으로 정렬하여 조회합니다.
     */
    // 1. SELECT -> SELECT DISTINCT 로 변경하여 중복된 Airplane 객체 제거
    // 2. JOIN FETCH a.seats 에 별칭 's'를 부여
    // 3. ORDER BY s.price ASC 로 정렬 기준 변경
    @Query("SELECT DISTINCT a FROM Airplane a JOIN FETCH a.seats s ORDER BY s.price ASC")
    List<Airplane> findAllWithSeatsOrderByPriceAsc();
}