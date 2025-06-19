package com.admin.CNU_airline_reservation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Spring Boot 애플리케이션의 시작점(Entry Point)이 되는 메인 클래스
 */
@EnableAsync // 애플리케이션 전체에서 @Async 어노테이션을 활성화하여 비동기 기능을 사용하도록 설정
@SpringBootApplication
public class CNUAirlineReservationApplication {

    public static void main(String[] args) {
        SpringApplication.run(CNUAirlineReservationApplication.class, args);
    }

}
