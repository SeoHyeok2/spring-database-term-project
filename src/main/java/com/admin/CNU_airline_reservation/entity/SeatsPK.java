package com.admin.CNU_airline_reservation.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class SeatsPK implements Serializable {

    private String flightNo;
    private LocalDateTime departureDateTime;
    private String seatClass;

    // equals and hashCode 구현...
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