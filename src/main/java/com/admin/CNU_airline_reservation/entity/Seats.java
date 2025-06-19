package com.admin.CNU_airline_reservation.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

// Seats 엔티티
@Entity
@Table(name = "Seats")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(SeatsPK.class)
public class Seats {

    // --- 복합 기본 키(PK)이자 외래 키(FK) ---
    // 이 필드들의 값으로 DB에 데이터가 저장/수정
    @Id
    private String flightNo; // 운항편명 (FK, 복합키 1)

    @Id
    private LocalDateTime departureDateTime; // 출발날짜 (FK, 복합키 2)

    @Id
    private String seatClass; // 좌석등급 (복합키 3)

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
    private Integer price; // 가격

    @Setter // 예약/취소 시 서비스 계층에서 좌석 수를 변경해야 하므로 Setter를 허용
    @Column(nullable = false)
    private Integer no_of_seats; // 남은 좌석 수

    // 빌더 패턴을 위한 생성자
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
