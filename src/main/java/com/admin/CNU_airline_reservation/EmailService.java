package com.admin.CNU_airline_reservation;

import com.admin.CNU_airline_reservation.dto.BoardingPassDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

/**
 * 탑승권 발송 등 이메일 관련 비즈니스 로직을 처리하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender; // 메일 발송용 인터페이스
    private final SpringTemplateEngine templateEngine; // HTML 생성을 위한 템플릿 엔진

    @Async // 메소드 비동기 실행 (메일 발송이 다른 작업에 영향을 주지 않음)
    public void sendBoardingPass(BoardingPassDTO boardingPassInfo) {
        try {
            // 메일 객체 생성
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            // 메일 내용 구성을 돕는 헬퍼 객체 생성 (UTF-8 인코딩)
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            // HTML 템플릿에 전달할 데이터 설정
            Context context = new Context();
            context.setVariable("customerName", boardingPassInfo.getCustomerName());
            context.setVariable("reservationTime", boardingPassInfo.getReservationTime());
            context.setVariable("airline", boardingPassInfo.getAirline());
            context.setVariable("flightNo", boardingPassInfo.getFlightNo());
            context.setVariable("seatClass", boardingPassInfo.getSeatClass());
            context.setVariable("departureAirport", boardingPassInfo.getDepartureAirport());
            context.setVariable("departureDateTime", boardingPassInfo.getDepartureDateTime());
            context.setVariable("arrivalAirport", boardingPassInfo.getArrivalAirport());
            context.setVariable("arrivalDateTime", boardingPassInfo.getArrivalDateTime());

            // 템플릿과 데이터를 결합하여 HTML 생성
            String html = templateEngine.process("email/boarding_pass", context);

            //  메일 정보 설정
            helper.setTo(boardingPassInfo.getCustomerEmail()); // 받는 사람
            helper.setSubject("[CNU 항공] " + boardingPassInfo.getFlightNo() + "편 예약 확정 및 탑승권 안내"); // 제목
            helper.setText(html, true); // 본문 (HTML)

            // 메일 발송
            javaMailSender.send(mimeMessage);

        } catch (MessagingException e) {
            // 메일 발송 실패 시 예외 처리
            throw new RuntimeException("메일 발송에 실패했습니다.", e);
        }
    }
}