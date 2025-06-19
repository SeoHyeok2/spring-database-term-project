package com.admin.CNU_airline_reservation.repository;

import com.admin.CNU_airline_reservation.dto.ReservationStats;
import com.admin.CNU_airline_reservation.entity.Reserve;
import com.admin.CNU_airline_reservation.entity.ReservePK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReserveRepository extends JpaRepository<Reserve, ReservePK> {

    /**
     * 특정 고객의 특정 기간 내 예약 목록 중, 취소되지 않은 '활성' 예약만 조회
     * NOT EXISTS 서브쿼리: Cancel 테이블에 동일한 키를 가진 데이터가 없는 예약만 필터링
     */
    @Query("SELECT r FROM Reserve r WHERE r.customer = :cno " +
            "AND r.reserveDateTime BETWEEN :startDate AND :endDate " +
            "AND NOT EXISTS (" +
            "  SELECT c FROM Cancel c WHERE c.customer = r.customer " +
            "  AND c.flightNo = r.flightNo " +
            "  AND c.departureDateTime = r.departureDateTime " +
            "  AND c.seatClass = r.seatClass" +
            ")")
    List<Reserve> findActiveReservationsByCustomerCnoAndDateRange(
            @Param("cno") String cno,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * 특정 고객의 모든 '활성' 예약 목록을 조회
     */
    @Query("SELECT r FROM Reserve r WHERE r.customer = :cno " +
            "AND NOT EXISTS (" +
            "  SELECT c FROM Cancel c WHERE c.customer = r.customer " +
            "  AND c.flightNo = r.flightNo " +
            "  AND c.departureDateTime = r.departureDateTime " +
            "  AND c.seatClass = r.seatClass" +
            ")")
    List<Reserve> findActiveReservationsByCustomerCno(@Param("cno") String cno);


    /**
     * 운항편명과 좌석등급별 예약 수 및 총 결제 금액에 대한 통계를 조회 (관리자용)
     * ROLLUP()와 같은 DB 고유 함수를 사용하기 위해 Native Query로 작성
     * ORDER BY GROUPING(): ROLLUP으로 생성된 집계 행이 결과의 마지막에 오도록 정렬
     */
    @Query(value = "SELECT " +
            "  CASE GROUPING(R.flight_no) " +
            "    WHEN 1 THEN '모든 운항편명' " +
            "    ELSE R.flight_no END AS 운항편명, " +
            "  CASE GROUPING(R.seat_class) " +
            "    WHEN 1 THEN '모든 좌석등급' " +
            "    ELSE R.seat_class END AS 좌석등급, " +
            "  COUNT(*) AS 예약수, " +
            "  SUM(R.payment) AS 지불금액 " +
            "FROM reserve R " + // 실제 테이블 이름(소문자) 사용
            "JOIN seats S ON R.flight_no = S.flight_no AND R.departure_date_time = S.departure_date_time AND R.seat_class = S.seat_class " +
            "GROUP BY ROLLUP(R.flight_no, R.seat_class) " +
            "ORDER BY GROUPING(R.flight_no), R.flight_no, GROUPING(R.seat_class), R.seat_class", nativeQuery = true)
    List<ReservationStats> findReservationStats();

    /**
     * findById(), save(), findAll(), delete() 등의 기본 메소드는
     * JpaRepository에 이미 구현되어 있으므로 따로 작성할 필요가 없음
     */
}