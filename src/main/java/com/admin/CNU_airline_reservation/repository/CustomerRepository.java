package com.admin.CNU_airline_reservation.repository;

import com.admin.CNU_airline_reservation.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
// 1. Customer의 PK 타입이 String이므로, JpaRepository의 두 번째 제네릭 타입을 String으로 지정
public interface CustomerRepository extends JpaRepository<Customer, String> {

    /**
     * 이메일과 비밀번호로 고객을 찾는 메소드 (로그인 기능)
     * 2. @Query 없이 메소드 이름만으로 쿼리를 자동 생성
     * (엔티티의 필드명 'email', 'passwd'와 일치해야 함)
     */
    Optional<Customer> findByEmailAndPasswd(String email, String passwd);

    /**
     * 이메일로 고객을 찾는 메소드 (중복 가입 확인 등)
     */
    Optional<Customer> findByEmail(String email);

    // 3. 아래 메소드들은 모두 삭제합니다.
    // findById(id) -> JpaRepository가 이미 기본 제공
    // saveMember(...) -> JPA에서는 repository.save(customer객체)를 사용하므로 불필요
}