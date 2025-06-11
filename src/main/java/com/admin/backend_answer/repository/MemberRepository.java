package com.admin.backend_answer.repository;

import com.admin.backend_answer.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    @Query("SELECT m FROM Member m WHERE m.memberEmail = :email AND m.memberPassword = :password")
    Optional<Member> findByMemberEmailAndMemberPassword(@Param("email") String email, @Param("password") String password);
    
    @Query("SELECT m FROM Member m WHERE m.memberId = :id")
    Optional<Member> findById(@Param("id") Long id);

    @Modifying
    @Query(value = "INSERT INTO member (member_name, member_email, member_password, passport_number) " +
            "VALUES (:memberName, :memberEmail, :memberPassword, :passportNumber)", nativeQuery = true)
    void saveMember(@Param("memberName") String memberName,
                    @Param("memberEmail") String memberEmail,
                    @Param("memberPassword") String memberPassword,
                    @Param("passportNumber") String passportNumber);
}
