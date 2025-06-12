package com.admin.CNU_airline_reservation.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class AirplanePK implements Serializable {

    private String flightNo;
    private LocalDateTime departureDateTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AirplanePK airplanePK = (AirplanePK) o;
        return Objects.equals(flightNo, airplanePK.flightNo) && Objects.equals(departureDateTime, airplanePK.departureDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(flightNo, departureDateTime);
    }
}