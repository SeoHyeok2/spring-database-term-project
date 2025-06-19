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

    /**
     * 특정 고객(cno)의 모든 취소 내역을 조회
     * Spring Data JPA의 쿼리 메소드 기능을 사용하여, 메소드 이름만으로 'findByCustomer' 쿼리를 자동 생성
     */
    List<Cancel> findByCustomer(@Param("cno") String cno);

    /**
     * 특정 고객의 특정 기간 내 취소 내역을 조회
     */
    List<Cancel> findByCustomerAndCancelDateTimeBetween(String cno, LocalDateTime startDate, LocalDateTime endDate);


    /**
     * 전체 취소 내역에 대해 순위를 매겨 통계 데이터를 조회 (관리자용)
     * ROW_NUMBER()와 같은 DB 고유 함수를 사용하기 위해 Native Query로 작성
     * ROW_NUMBER()를 사용하여 취소 시간(cancel_date_time)순으로 순위 매김
     */
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

    /**
     * findById(), save(), findAll(), delete() 등의 기본 메소드는
     * JpaRepository에 이미 구현되어 있으므로 따로 작성할 필요가 없음
     */
}
