package com.admin.CNU_airline_reservation.dto;

/**
 * 로그인 요청 시 사용자의 아이디와 비밀번호를 전달하기 위한 DTO (Data Transfer Object)
 * 데이터를 담는 목적의 객체를 간결하게 표현
 * 컴파일러가 자동으로 생성자, getter(cno(), password()), equals(), hashCode(), toString() 메소드를 생성
 */
public record LoginRequest(
        String cno,
        String password
) {
}
