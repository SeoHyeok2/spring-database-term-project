package com.admin.CNU_airline_reservation.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// Customer 엔티티
@Entity
@Table(name = "Customer")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Customer {

    @Id
    private String cno; // 고객번호 (PK)

    @Column(nullable = false)
    private String name; // 이름

    @Column(nullable = false)
    private String passwd; // 비밀번호

    @Column(nullable = false, unique = true)
    private String email; // 이메일

    @Column(unique = true)
    private String passportNumber; // 여권번호

    // 이 고객이 한 예약 목록 (1:N 관계)
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reserve> reserves = new ArrayList<>();

    // 이 고객의 취소 내역 목록 (1:N 관계)
    @OneToMany(mappedBy = "customerRel", cascade = CascadeType.ALL)
    private List<Cancel> cancels = new ArrayList<>();

    // 빌더 패턴을 위한 생성자
    @Builder
    public Customer(String cno, String name, String passwd, String email, String passportNumber) {
        this.cno = cno;
        this.name = name;
        this.passwd = passwd;
        this.email = email;
        this.passportNumber = passportNumber;
    }
}