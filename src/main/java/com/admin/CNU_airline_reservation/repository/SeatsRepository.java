package com.admin.CNU_airline_reservation.repository;

import com.admin.CNU_airline_reservation.entity.Seats;
import com.admin.CNU_airline_reservation.entity.SeatsPK; // SeatsId를 임포트
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
// 1. 엔티티는 Seats, ID 타입은 복합 키 클래스인 SeatsId로 지정합니다.
public interface SeatsRepository extends JpaRepository<Seats, SeatsPK> {
    // save(seats), deleteById(seatsId), findById(seatsId) 등은 JpaRepository가 기본 제공합니다.
}