package com.admin.CNU_airline_reservation.repository;

import com.admin.CNU_airline_reservation.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {

    /**
     * 회원번호(cno)와 비밀번호(passwd)로 고객을 조회
     * Spring Data JPA의 쿼리 메소드 기능을 사용하여, 메소드 이름만으로 'findByCnoAndPasswd' 쿼리를 자동 생성
     */
    Optional<Customer> findByCnoAndPasswd(String cno, String passwd);

    /**
     * findById(), save(), findAll(), delete() 등의 기본 메소드는
     * JpaRepository에 이미 구현되어 있으므로 따로 작성할 필요가 없음
     */
}