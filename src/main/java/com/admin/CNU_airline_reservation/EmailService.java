package com.admin.CNU_airline_reservation;

import com.admin.CNU_airline_reservation.dto.BoardingPassDTO; // DTO 임포트
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

    @Async
    // 파라미터가 Reserve 엔티티가 아닌 BoardingPassDTO로 변경됨
    public void sendBoardingPass(BoardingPassDTO boardingPassInfo) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            Context context = new Context();
            // DTO에 있는 값을 context에 담습니다.
            context.setVariable("customerName", boardingPassInfo.getCustomerName());
            context.setVariable("reservationTime", boardingPassInfo.getReservationTime());
            context.setVariable("airline", boardingPassInfo.getAirline());
            context.setVariable("flightNo", boardingPassInfo.getFlightNo());
            context.setVariable("seatClass", boardingPassInfo.getSeatClass());
            context.setVariable("departureAirport", boardingPassInfo.getDepartureAirport());
            context.setVariable("departureDateTime", boardingPassInfo.getDepartureDateTime());
            context.setVariable("arrivalAirport", boardingPassInfo.getArrivalAirport());
            context.setVariable("arrivalDateTime", boardingPassInfo.getArrivalDateTime());

            String html = templateEngine.process("email/boarding_pass", context);

            helper.setTo(boardingPassInfo.getCustomerEmail()); // DTO에서 수신자 이메일 가져옴
            helper.setSubject("[CNU 항공] " + boardingPassInfo.getFlightNo() + "편 예약 확정 및 탑승권 안내");
            helper.setText(html, true);

            javaMailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new RuntimeException("메일 발송에 실패했습니다.", e);
        }
    }
}