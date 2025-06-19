package com.admin.CNU_airline_reservation.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

// Seats 엔티티의 복합 기본 키를 정의하는 클래스
@NoArgsConstructor
@AllArgsConstructor
public class SeatsPK implements Serializable {

    private String flightNo;
    private LocalDateTime departureDateTime;
    private String seatClass;

    // JPA가 영속성 컨텍스트 내에서 엔티티의 동일성을 비교하기 위해 사용하는 필수 메소드들
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SeatsPK seatsPK = (SeatsPK) o;
        return Objects.equals(flightNo, seatsPK.flightNo) && Objects.equals(departureDateTime, seatsPK.departureDateTime) && Objects.equals(seatClass, seatsPK.seatClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(flightNo, departureDateTime, seatClass);
    }
}