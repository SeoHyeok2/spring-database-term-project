package com.admin.CNU_airline_reservation.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Seats")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(SeatsPK.class)
public class Seats {

    // --- 여기부터 복합 기본 키(PK)이자 외래 키(FK) ---

    @Id
    private String flightNo;

    @Id
    private LocalDateTime departureDateTime;

    @Id
    private String seatClass;

    // --- 관계 설정을 위한 필드 ---
    // 이 필드는 '읽기 전용'으로, 실제 DB 삽입/수정은 위의 PK 필드들이 담당
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "flightNo", referencedColumnName = "flightNo", insertable = false, updatable = false),
            @JoinColumn(name = "departureDateTime", referencedColumnName = "departureDateTime", insertable = false, updatable = false)
    })
    private Airplane airplane;


    // --- 일반 데이터 필드 ---
    @Column(nullable = false)
    private Integer price;

    @Setter
    @Column(nullable = false)
    private Integer no_of_seats;

    @Builder
    public Seats(String flightNo, LocalDateTime departureDateTime, String seatClass, Airplane airplane, Integer price, Integer no_of_seats) {
        this.flightNo = flightNo;
        this.departureDateTime = departureDateTime;
        this.seatClass = seatClass;
        this.airplane = airplane;
        this.price = price;
        this.no_of_seats = no_of_seats;
    }
}
