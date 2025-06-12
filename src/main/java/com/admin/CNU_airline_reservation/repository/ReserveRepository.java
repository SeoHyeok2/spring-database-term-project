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
     * 특정 고객의 모든 예약 목록을 최신순으로 조회합니다.
     * 연관된 엔티티 정보(customer, seats)를 한 번에 가져오기 위해 JOIN FETCH 사용
     * 2. 엔티티에 정의된 필드명(customer, cno)을 정확히 사용해야 합니다.
     */
    @Query("SELECT r FROM Reserve r " +
            "JOIN FETCH r.customerRel c " +
            "JOIN FETCH r.seatsRel s " +
            "WHERE c.cno = :cno " +
            "ORDER BY r.reserveDateTime DESC")
    List<Reserve> findByCustomerCnoWithDetails(@Param("cno") String cno);

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