package com.admin.CNU_airline_reservation.repository;

import com.admin.CNU_airline_reservation.entity.Customer;
import com.admin.CNU_airline_reservation.entity.Reserve;
import com.admin.CNU_airline_reservation.entity.ReservePK; // 1. ID 타입을 ReserveId로 변경
import com.admin.CNU_airline_reservation.entity.Seats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
// 1. Reserve의 PK는 ReserveId이므로, 제네릭 타입을 <Reserve, ReserveId>로 지정
public interface ReserveRepository extends JpaRepository<Reserve, ReservePK> {

    /**
     * 특정 고객의 예약 목록 중, Cancel 테이블에 존재하지 않는 (취소되지 않은) 예약만 조회합니다.
     */
    @Query("SELECT r FROM Reserve r WHERE r.customer = :cno AND NOT EXISTS (" +
            "  SELECT c FROM Cancel c WHERE c.customer = r.customer " +
            "  AND c.flightNo = r.flightNo " +
            "  AND c.departureDateTime = r.departureDateTime " +
            "  AND c.seatClass = r.seatClass" +
            ")")
    List<Reserve> findActiveReservationsByCustomerCno(@Param("cno") String cno);

    /**
     * 특정 고객의 특정 좌석에 대한 가장 최근 예약을 조회합니다.
     * 3. @Query 대신 쿼리 메소드(Query Method)를 사용하면 훨씬 간결합니다.
     */
    Optional<Reserve> findFirstByCustomerRelAndSeatsRelOrderByReserveDateTimeDesc(Customer customer, Seats seats);

    // 4. 아래 메소드들은 JpaRepository의 기본 기능을 사용하므로 모두 삭제합니다.
    // findById(id) -> 기본 제공
    // saveReservation(...) -> repository.save(entity) 사용
    // deleteReservation(...) -> repository.delete(entity) 또는 deleteById(id) 사용
}