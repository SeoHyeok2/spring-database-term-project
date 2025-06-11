package com.admin.backend_answer.repository;

import com.admin.backend_answer.entity.Reservation;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query("SELECT r FROM Reservation r " +
           "JOIN FETCH r.flight f " +
           "JOIN FETCH r.member m " +
           "WHERE r.member.memberId = :memberId " +
           "ORDER BY r.reservationDate DESC")
    List<Reservation> findByMemberMemberId(@Param("memberId") Long memberId);
    
    @Query("SELECT r FROM Reservation r " +
           "JOIN FETCH r.flight f " +
           "JOIN FETCH r.member m " +
           "WHERE r.reservationId = :id")
    Optional<Reservation> findById(@Param("id") Long id);

    @Query("SELECT r FROM Reservation r WHERE r.reservationId = :reservationId AND r.member.memberId = :memberId")
    Reservation findByIdAndMemberId(@Param("reservationId") Long reservationId, @Param("memberId") Long memberId);

    @Modifying
    @Query("DELETE FROM Reservation r WHERE r.reservationId = :reservationId AND r.member.memberId = :memberId")
    void deleteReservation(@Param("reservationId") Long reservationId, @Param("memberId") Long memberId);

    @Modifying
    @Query(value = "INSERT INTO reservation (member_member_id, flight_flight_id, price, reservation_date) " +
            "VALUES (:memberId, :flightId, :price, :reservationDate)", nativeQuery = true)
    void saveReservation(@Param("memberId") Long memberId,
                         @Param("flightId") Long flightId,
                         @Param("price") Long price,
                         @Param("reservationDate") LocalDateTime reservationDate);

    @Query("SELECT r FROM Reservation r " +
            "WHERE r.member.memberId = :memberId AND r.flight.flightId = :flightId " +
            "ORDER BY r.reservationDate DESC LIMIT 1")
    Reservation findLatestReservation(@Param("memberId") Long memberId,
                                      @Param("flightId") Long flightId);
}
