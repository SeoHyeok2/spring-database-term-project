package com.admin.CNU_airline_reservation.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class ReservePK implements Serializable {

    // Customer의 PK와 Seats의 PK를 구성하는 모든 필드를 기본 타입으로 선언
    private String customer; // Customer의 PK(cno) 타입이 String이므로
    private String flightNo;
    private LocalDateTime departureDateTime;
    private String seatClass;

    // equals and hashCode 구현...
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReservePK reservePK = (ReservePK) o;
        return Objects.equals(customer, reservePK.customer) &&
                Objects.equals(flightNo, reservePK.flightNo) &&
                Objects.equals(departureDateTime, reservePK.departureDateTime) &&
                Objects.equals(seatClass, reservePK.seatClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customer, flightNo, departureDateTime, seatClass);
    }
}