package com.admin.backend_answer.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;
    private Long price;
    private LocalDateTime reservationDate;

    @ManyToOne
    private Member member;

    @ManyToOne
    private Flight flight;

    @Builder
    public Reservation(Long price, LocalDateTime reservationDate, Member member, Flight flight) {
        this.price = price;
        this.reservationDate = reservationDate;
        this.member = member;
        this.flight = flight;
    }
}
