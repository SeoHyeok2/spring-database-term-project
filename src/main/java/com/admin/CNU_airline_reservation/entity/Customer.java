package com.admin.CNU_airline_reservation.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Customer")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Customer {

    @Id
    private String cno;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String passwd;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(unique = true)
    private String passportNumber;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reserve> reserves = new ArrayList<>();

//    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Cancel> cancels = new ArrayList<>();

    @Builder
    public Customer(String cno, String name, String passwd, String email, String passportNumber) {
        this.cno = cno;
        this.name = name;
        this.passwd = passwd;
        this.email = email;
        this.passportNumber = passportNumber;
    }
}