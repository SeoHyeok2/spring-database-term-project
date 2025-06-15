package com.admin.CNU_airline_reservation.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Reserve")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(ReservePK.class)
public class Reserve {

    @Id
    @Column(name = "cno", length = 50) // Customer의 cno와 매핑될 컬럼
    private String customer;

    @Id
    private String flightNo;

    @Id
    private LocalDateTime departureDateTime;

    @Id
    private String seatClass;


    // --- 관계 설정을 위한 필드 (조회용) ---
    // 실제 DB 삽입/수정은 위의 PK 필드들이 담당합니다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cno", referencedColumnName = "cno", insertable = false, updatable = false)
    private Customer customerRel; // 이름 충돌을 피하기 위해 Rel(Relation) 접미사 추가

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "flightNo", referencedColumnName = "flightNo", insertable = false, updatable = false),
            @JoinColumn(name = "departureDateTime", referencedColumnName = "departureDateTime", insertable = false, updatable = false),
            @JoinColumn(name = "seatClass", referencedColumnName = "seatClass", insertable = false, updatable = false)
    })
    private Seats seatsRel; // 이름 충돌을 피하기 위해 Rel(Relation) 접미사 추가


    // --- 일반 데이터 필드 ---
    @Column(nullable = false)
    private Integer payment;

    @Column(nullable = false)
    private LocalDateTime reserveDateTime;


    @Builder
    public Reserve(String customer, String flightNo, LocalDateTime departureDateTime, String seatClass,
                   Customer customerRel, Seats seatsRel, Integer payment, LocalDateTime reserveDateTime) {
        this.customer = customer;
        this.flightNo = flightNo;
        this.departureDateTime = departureDateTime;
        this.seatClass = seatClass;
        this.customerRel = customerRel;
        this.seatsRel = seatsRel;
        this.payment = payment;
        this.reserveDateTime = reserveDateTime;
    }
}
