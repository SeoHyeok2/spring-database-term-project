package com.admin.CNU_airline_reservation;


import com.admin.CNU_airline_reservation.dto.BoardingPassDTO;
import com.admin.CNU_airline_reservation.dto.CancellationStats;
import com.admin.CNU_airline_reservation.dto.ReservationStats;
import com.admin.CNU_airline_reservation.entity.*;
import com.admin.CNU_airline_reservation.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

/**
 * 애플리케이션의 핵심 비즈니스 로직을 처리하는 메인 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class MainService {
    private final AirplaneRepository airplaneRepository;
    private final CustomerRepository customerRepository;
    private final SeatsRepository seatsRepository;
    private final ReserveRepository reserveRepository;
    private final CancelRepository cancelRepository;

    private final EmailService emailService;

    /**
     * 메인 페이지에 보여줄 모든 항공편 목록을 조회
     */
    public List<Airplane> findAllAirplanes() {
        return airplaneRepository.findAllWithSeatsOrderByPriceAsc();
    }

    /**
     * 로그인을 처리
     */
    public Optional<Customer> login(String cno, String password) {
        return customerRepository.findByCnoAndPasswd(cno, password);
    }

    /**
     * 사용자의 검색 조건에 맞는 항공편 목록을 조회
     */
    @Transactional(readOnly = true) // 읽기 전용 트랜잭션으로 성능 최적화
    public List<Airplane> searchAirplanes(String departureAirport, String arrivalAirport, LocalDate departureDate, String seatClass, String sortOption) {
        // 정렬 옵션에 따라 다른 리포지토리 메소드를 호출
        if ("time".equals(sortOption)) {
            // 출발 시간순 정렬
            return airplaneRepository.findAirplanesByDateOrderByDepartureDateTimeAsc(
                    departureAirport, arrivalAirport, departureDate, seatClass
            );

        } else if ("seats".equals(sortOption)) {
            // 잔여 좌석순 정렬
            return airplaneRepository.findAirplanesByDateOrderByNoOfSeatsDesc(
                    departureAirport, arrivalAirport, departureDate, seatClass
            );
        } else {
            // 기본값: 가격순 정렬
            return airplaneRepository.findAirplanesByDateOrderByPriceAsc(
                    departureAirport, arrivalAirport, departureDate, seatClass
            );
        }
    }

    /**
     * 항공편을 예약하고, 좌석 재고를 줄이며, 예약 완료 이메일을 발송
     */
    @Transactional // 여러 DB 변경 작업을 하나의 트랜잭션으로 묶어 데이터 일관성을 보장
    public Reserve makeReservation(String cno, String flightNo, LocalDateTime departureDateTime, String seatClass) {

        // 고객 및 예약할 좌석 정보 조회
        Customer customer = customerRepository.findById(cno)
                .orElseThrow(() -> new IllegalArgumentException("해당 고객을 찾을 수 없습니다. cno=" + cno));
        SeatsPK seatsPK = new SeatsPK(flightNo, departureDateTime, seatClass);
        Seats seats = seatsRepository.findById(seatsPK)
                .orElseThrow(() -> new IllegalArgumentException("해당 좌석 정보를 찾을 수 없습니다."));

        // 좌석 재고 확인 및 차감
        int currentSeats = seats.getNo_of_seats(); // 현재 좌석 수
        // 재고가 0 이하인지 확인
        if (currentSeats <= 0) {
            throw new IllegalStateException("남은 좌석이 없습니다.");
        }
        // 재고가 있다면 1을 감소시킨 값을 setter로 설정
        seats.setNo_of_seats(currentSeats - 1);


        // 새로운 Reserve 엔티티 생성
        Reserve newReserve = Reserve.builder()
                .customer(customer.getCno())
                .flightNo(seats.getFlightNo())
                .departureDateTime(seats.getDepartureDateTime())
                .seatClass(seats.getSeatClass())
                .customerRel(customer)
                .seatsRel(seats)
                .payment(seats.getPrice())
                .reserveDateTime(LocalDateTime.now())
                .build();

        // 예약 정보 저장
        Reserve savedReserve = reserveRepository.save(newReserve);

        // 이메일 발송용 DTO 생성
        BoardingPassDTO boardingPassInfo = BoardingPassDTO.builder()
                .customerName(savedReserve.getCustomerRel().getName())
                .customerEmail(savedReserve.getCustomerRel().getEmail())
                .reservationTime(savedReserve.getReserveDateTime())
                .airline(savedReserve.getSeatsRel().getAirplane().getAirline())
                .flightNo(savedReserve.getFlightNo())
                .seatClass(savedReserve.getSeatClass())
                .departureAirport(savedReserve.getSeatsRel().getAirplane().getDepartureAirport())
                .departureDateTime(savedReserve.getDepartureDateTime())
                .arrivalAirport(savedReserve.getSeatsRel().getAirplane().getArrivalAirport())
                .arrivalDateTime(savedReserve.getSeatsRel().getAirplane().getArrivalDateTime())
                .build();

        // 이메일 발송 서비스 호출 (비동기 처리)
        emailService.sendBoardingPass(boardingPassInfo);

        return savedReserve;
    }

    /**
     * 특정 예약 한 건을 조회
     */
    public Reserve getReservation(String cno, String flightNo, LocalDateTime departureDateTime, String seatClass) {
        ReservePK reservePK = new ReservePK(cno, flightNo, departureDateTime, seatClass);
        return reserveRepository.findById(reservePK)
                .orElseThrow(() -> new IllegalArgumentException("해당 예약을 찾을 수 없습니다."));
    }

    /**
     * 특정 사용자의 '활성' 예약 목록을 조회 (취소된 예약 제외)
     */
    public List<Reserve> getMyReservations(String cno, LocalDate startDate, LocalDate endDate) {
        // 날짜 검색 조건이 있는지에 따라 다른 메소드 호출
        if (startDate != null && endDate != null) {
            // 날짜 범위가 지정된 경우
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay().minusNanos(1);
            return reserveRepository.findActiveReservationsByCustomerCnoAndDateRange(cno, startDateTime, endDateTime);
        } else {
            // 날짜 범위가 없는 경우
            return reserveRepository.findActiveReservationsByCustomerCno(cno);
        }
    }

    /**
     * 예약을 취소하고, 좌석 재고를 늘리며, 위약금을 계산하여 취소 기록을 생성
     */
    @Transactional
    public Cancel cancelReservation(String cno, String flightNo, LocalDateTime departureDateTime, String seatClass) {
        // 취소할 예약이 유효한지 확인
        ReservePK reservePK = new ReservePK(cno, flightNo, departureDateTime, seatClass);
        Reserve reserveToCancel = reserveRepository.findById(reservePK)
                .orElseThrow(() -> new IllegalArgumentException("취소할 예약 정보를 찾을 수 없습니다."));

        // 이미 취소되었는지 확인
        CancelPK cancelPK = new CancelPK(cno, flightNo, departureDateTime, seatClass);
        if (cancelRepository.existsById(cancelPK)) {
            throw new IllegalStateException("이미 취소된 예약입니다.");
        }

        // 위약금 계산
        LocalDate today = LocalDate.now(); // 취소 요청이 들어온 오늘 날짜
        LocalDate departureDate = reserveToCancel.getDepartureDateTime().toLocalDate(); // 항공편의 출발 날짜 (시간 제외)

        // 출발일까지 남은 일수 계산
        long daysUntilDeparture = ChronoUnit.DAYS.between(today, departureDate);

        int payment = reserveToCancel.getPayment(); // 원래 결제했던 금액
        int penaltyFee = 0; // 위약금

        if (daysUntilDeparture >= 15) {
            // 15일 이상 남았을 경우
            penaltyFee = 150000;
        } else if (daysUntilDeparture >= 4) {
            // 4일에서 14일 사이 남았을 경우
            penaltyFee = 180000;
        } else if (daysUntilDeparture >= 1) {
            // 1일에서 3일 사이 남았을 경우
            penaltyFee = 250000;
        } else {
            // 당일 또는 날짜가 지난 경우
            penaltyFee = payment; // 전액 위약금
        }

        // 최종 환불액 계산 (환불액이 음수가 되지 않도록 처리)
        int finalRefundAmount = Math.max(0, payment - penaltyFee);
        // ----------------------------------------------------


        // 좌석 재고 1 증가
        Seats seats = reserveToCancel.getSeatsRel();
        seats.setNo_of_seats(seats.getNo_of_seats() + 1);
        seatsRepository.save(seats); // 변경된 좌석 정보를 DB에 반영

        // Cancel 엔티티 생성 및 저장
        Cancel newCancel = Cancel.builder()
                .customer(reserveToCancel.getCustomer())
                .flightNo(reserveToCancel.getFlightNo())
                .departureDateTime(reserveToCancel.getDepartureDateTime())
                .seatClass(reserveToCancel.getSeatClass())
                .customerRel(reserveToCancel.getCustomerRel())
                .seatsRel(reserveToCancel.getSeatsRel())
                .refund(finalRefundAmount)
                .cancelDateTime(LocalDateTime.now())
                .build();

        return cancelRepository.save(newCancel);
    }

    /**
     * 특정 취소 내역 한 건을 조회
     */
    public Cancel getCancellation(String cno, String flightNo, LocalDateTime departureDateTime, String seatClass) {
        CancelPK cancelId = new CancelPK(cno, flightNo, departureDateTime, seatClass);
        return cancelRepository.findById(cancelId)
                .orElseThrow(() -> new IllegalArgumentException("해당 취소 내역을 찾을 수 없습니다."));
    }

    /**
     * 특정 사용자의 취소 내역 목록을 조회
     */
    public List<Cancel> getMyCancellations(String cno, LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null) {
            // 날짜 범위가 지정된 경우
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay().minusNanos(1);
            return cancelRepository.findByCustomerAndCancelDateTimeBetween(cno, startDateTime, endDateTime);
        } else {
            // 날짜 범위가 없는 경우
            return cancelRepository.findByCustomer(cno);
        }
    }

    /**
     * 예약 통계를 조회 (관리자용)
     */
    public List<ReservationStats> getReservationStatistics() {
        return reserveRepository.findReservationStats();
    }

    /**
     * 취소 통계를 조회 (관리자용)
     */
    public List<CancellationStats> getCancellationStatistics() {
        return cancelRepository.findCancellationStats();
    }

}
