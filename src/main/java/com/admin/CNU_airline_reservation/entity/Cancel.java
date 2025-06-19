package com.admin.CNU_airline_reservation.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// Cancel 엔티티
@Entity
@Table(name = "Cancel")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(CancelPK.class) // CancelPK 클래스를 PK로 지정
public class Cancel {

    // --- 복합 기본 키(PK)를 구성하는 필드들 ---
    // 이 필드들의 값으로 DB에 데이터가 저장/수정
    @Id
    @Column(name = "cno", length = 50)
    private String customer; // 고객번호 (FK, 복합 키 1)

    @Id
    private String flightNo; // 운항편명 (FK, 복합 키 2)

    @Id
    private LocalDateTime departureDateTime; // 출발날짜 (FK, 복합 키 3)

    @Id
    private String seatClass; // 좌석등급 (FK, 복합 키 4)

    // --- 관계 설정을 위한 필드 ---
    // 이 필드는 '읽기 전용'으로, 실제 DB 삽입/수정은 위의 PK 필드들이 담당
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cno", referencedColumnName = "cno", insertable = false, updatable = false)
    private Customer customerRel; // Customer 엔티티와의 관계

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "flightNo", referencedColumnName = "flightNo", insertable = false, updatable = false),
            @JoinColumn(name = "departureDateTime", referencedColumnName = "departureDateTime", insertable = false, updatable = false),
            @JoinColumn(name = "seatClass", referencedColumnName = "seatClass", insertable = false, updatable = false)
    })
    private Seats seatsRel; // Seats 엔티티와의 관계

    // --- 일반 데이터 필드 ---
    @Column(nullable = false)
    private Integer refund; // 환불금액

    @Column(nullable = false)
    private LocalDateTime cancelDateTime; // 취소날짜

    // 빌더 패턴을 위한 생성자
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