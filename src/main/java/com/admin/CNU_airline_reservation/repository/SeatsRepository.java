package com.admin.CNU_airline_reservation.repository;

import com.admin.CNU_airline_reservation.entity.Seats;
import com.admin.CNU_airline_reservation.entity.SeatsPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatsRepository extends JpaRepository<Seats, SeatsPK> {
    /**
     * findById(), save(), findAll(), delete() 등의 기본 메소드는
     * JpaRepository에 이미 구현되어 있으므로 따로 작성할 필요가 없음
     */
}