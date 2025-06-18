package com.admin.CNU_airline_reservation.repository;

import com.admin.CNU_airline_reservation.dto.CancellationStats;
import com.admin.CNU_airline_reservation.entity.Cancel;
import com.admin.CNU_airline_reservation.entity.CancelPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CancelRepository extends JpaRepository<Cancel, CancelPK> {
    // 특정 고객의 모든 취소 내역을 조회 (Customer의 cno 기준)
    List<Cancel> findByCustomer(@Param("cno") String cno);

    // 날짜 범위로 조회하는 새로운 메소드 (쿼리 메소드로 간단히 생성 가능)
    List<Cancel> findByCustomerAndCancelDateTimeBetween(String cno, LocalDateTime startDate, LocalDateTime endDate);

    @Query(value = "SELECT " +
            "  A.flight_no AS 운항편명, " +
            "  C.cno AS 회원번호, " +
            "  C.seat_class AS 좌석등급, " +
            "  C.refund AS 환불금액, " +
            "  ROW_NUMBER() OVER (ORDER BY C.cancel_date_time ASC) AS 취소순위 " +
            "FROM airplane A " +
            "JOIN cancel C ON A.flight_no = C.flight_no AND A.departure_date_time = C.departure_date_time " +
            "ORDER BY 취소순위", nativeQuery = true)
    List<CancellationStats> findCancellationStats();
}
