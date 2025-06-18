package com.admin.CNU_airline_reservation.entity; // 실제 패키지 경로

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class CancelPK implements Serializable {

    private String customer;
    private String flightNo;
    private LocalDateTime departureDateTime;
    private String seatClass;

    // equals and hashCode 구현...
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CancelPK cancelPK = (CancelPK) o;
        return Objects.equals(customer, cancelPK.customer) &&
                Objects.equals(flightNo, cancelPK.flightNo) &&
                Objects.equals(departureDateTime, cancelPK.departureDateTime) &&
                Objects.equals(seatClass, cancelPK.seatClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customer, flightNo, departureDateTime, seatClass);
    }
}