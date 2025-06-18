package com.admin.CNU_airline_reservation.repository;

import com.admin.CNU_airline_reservation.entity.Cancel;
import com.admin.CNU_airline_reservation.entity.CancelPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CancelRepository extends JpaRepository<Cancel, CancelPK> {
    // 특정 고객의 모든 취소 내역을 조회 (Customer의 cno 기준)
    List<Cancel> findByCustomer(@Param("cno") String cno);
}
