package com.admin.CNU_airline_reservation.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Cancel")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(CancelPK.class) // CancelPK 클래스를 PK로 지정
public class Cancel {

    // --- 복합 기본 키(PK)이자 외래 키(FK) ---
    @Id
    @Column(name = "cno", length = 50)
    private String customer;

    @Id
    private String flightNo;

    @Id
    private LocalDateTime departureDateTime;

    @Id
    private String seatClass;

    // --- 관계 설정을 위한 조회용 필드 ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cno", referencedColumnName = "cno", insertable = false, updatable = false)
    private Customer customerRel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "flightNo", referencedColumnName = "flightNo", insertable = false, updatable = false),
            @JoinColumn(name = "departureDateTime", referencedColumnName = "departureDateTime", insertable = false, updatable = false),
            @JoinColumn(name = "seatClass", referencedColumnName = "seatClass", insertable = false, updatable = false)
    })
    private Seats seatsRel;

    // --- 일반 데이터 필드 ---
    @Column(nullable = false)
    private Integer refund; // 환불 금액

    @Column(nullable = false)
    private LocalDateTime cancelDateTime; // 취소 시각

    @Builder
    public Cancel(String customer, String flightNo, LocalDateTime departureDateTime, String seatClass,
                  Customer customerRel, Seats seatsRel, Integer refund, LocalDateTime cancelDateTime) {
        this.customer = customer;
        this.flightNo = flightNo;
        this.departureDateTime = departureDateTime;
        this.seatClass = seatClass;
        this.customerRel = customerRel;
        this.seatsRel = seatsRel;
        this.refund = refund;
        this.cancelDateTime = cancelDateTime;
    }
}