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
     * 조건(출발공항, 도착공항, 출발날짜, 좌석등급)에 맞는 항공편을 좌석 정보와 함께 조회하고, '가격' 오름차순으로 정렬
     */
    @Query("SELECT DISTINCT a FROM Airplane a JOIN FETCH a.seats s " +
            "WHERE a.departureAirport = :departureAirport " +
            "AND a.arrivalAirport = :arrivalAirport " +
            "AND FUNCTION('DATE', a.departureDateTime) = :departureDate " +
            "AND s.seatClass = :seatClass " +
            "ORDER BY s.price ASC")
    List<Airplane> findAirplanesByDateOrderByPriceAsc(
            @Param("departureAirport") String departureAirport,
            @Param("arrivalAirport") String arrivalAirport,
            @Param("departureDate") LocalDate departureDate,
            @Param("seatClass") String seatClass
    );

    /**
     * 조건(출발공항, 도착공항, 출발날짜, 좌석등급)에 맞는 항공편을 좌석 정보와 함께 조회하고, '출발 시간' 오름차순으로 정렬
     */
    @Query("SELECT DISTINCT a FROM Airplane a JOIN FETCH a.seats s " +
            "WHERE a.departureAirport = :departureAirport " +
            "AND a.arrivalAirport = :arrivalAirport " +
            "AND FUNCTION('DATE', a.departureDateTime) = :departureDate " +
            "AND s.seatClass = :seatClass " +
            "ORDER BY a.departureDateTime ASC")
    List<Airplane> findAirplanesByDateOrderByDepartureDateTimeAsc(
            @Param("departureAirport") String departureAirport,
            @Param("arrivalAirport") String arrivalAirport,
            @Param("departureDate") LocalDate departureDate,
            @Param("seatClass") String seatClass
    );

    /**
     * 조건(출발공항, 도착공항, 출발날짜, 좌석등급)에 맞는 항공편을 좌석 정보와 함께 조회하고, '남은 좌석 수' 내림차순으로 정렬
     */
    @Query("SELECT DISTINCT a FROM Airplane a JOIN FETCH a.seats s " +
            "WHERE a.departureAirport = :departureAirport " +
            "AND a.arrivalAirport = :arrivalAirport " +
            "AND FUNCTION('DATE', a.departureDateTime) = :departureDate " +
            "AND s.seatClass = :seatClass " +
            "ORDER BY s.no_of_seats DESC")
    List<Airplane> findAirplanesByDateOrderByNoOfSeatsDesc(
            @Param("departureAirport") String departureAirport,
            @Param("arrivalAirport") String arrivalAirport,
            @Param("departureDate") LocalDate departureDate,
            @Param("seatClass") String seatClass
    );

    /**
     * 모든 항공편을 좌석 정보와 함께 조회하고, '가격' 오름차순으로 정렬
     */
    @Query("SELECT DISTINCT a FROM Airplane a JOIN FETCH a.seats s ORDER BY s.price ASC")
    List<Airplane> findAllWithSeatsOrderByPriceAsc();

    /**
     * findById(), save(), findAll(), delete() 등의 기본 메소드는
     * JpaRepository에 이미 구현되어 있으므로 따로 작성할 필요가 없음
     */
}