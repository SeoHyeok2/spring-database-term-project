package com.admin.backend_answer.repository;

import com.admin.backend_answer.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {
    @Query("SELECT f FROM Flight f " +
           "WHERE f.departureAirport = :departureAirport " +
           "AND f.arrivalAirport = :arrivalAirport " +
           "AND DATE(f.departureTime) = :departureDate " +
           "ORDER BY f.arrivalTime ASC")
    List<Flight> findByDepartureAirportAndArrivalAirportAndDepartureDate(
            @Param("departureAirport") String departureAirport,
            @Param("arrivalAirport") String arrivalAirport,
            @Param("departureDate") LocalDate departureDate
    );
    
    @Query("SELECT f FROM Flight f WHERE f.flightId = :id")
    Optional<Flight> findById(@Param("id") Long id);
    
    @Query("SELECT f FROM Flight f")
    List<Flight> findAll();

    @Query("SELECT f FROM Flight f ORDER BY f.departureTime ASC")
    List<Flight> findAllOrderByDepartureTime();
}
