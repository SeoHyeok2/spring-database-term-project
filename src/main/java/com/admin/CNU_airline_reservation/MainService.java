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

@Service
@RequiredArgsConstructor
public class MainService {
    private final AirplaneRepository airplaneRepository;
    private final CustomerRepository customerRepository;
    private final SeatsRepository seatsRepository;
    private final ReserveRepository reserveRepository;
    private final CancelRepository cancelRepository;

    private final EmailService emailService;

    public List<Airplane> findAllAirplanes() {
        return airplaneRepository.findAllWithSeatsOrderByPriceAsc();
    }

    // 1. 반환 타입을 Optional<Customer>로 변경
    public Optional<Customer> login(String cno, String password) {
        // 2. 예외를 던지는 if문 삭제
        // 3. 리포지토리가 반환한 Optional 객체를 그대로 컨트롤러에 전달
        return customerRepository.findByCnoAndPasswd(cno, password);
    }

    @Transactional(readOnly = true)
    public List<Airplane> searchAirplanes(String departureAirport, String arrivalAirport, LocalDate departureDate, String sortOption) {

        // 정렬 옵션에 따라 호출할 Repository 메소드를 분기 처리합니다.
        if ("time".equals(sortOption)) {
            // 출발 시간순 정렬
            return airplaneRepository.findAirplanesByDateOrderByDepartureDateTimeAsc(
                    departureAirport, arrivalAirport, departureDate
            );

        } else if ("seats".equals(sortOption)) {
            // 잔여 좌석순 정렬
            return airplaneRepository.findAirplanesByDateOrderByNoOfSeatsDesc(
                    departureAirport, arrivalAirport, departureDate
            );
        } else {
            // 기본값: 요금순 정렬
            return airplaneRepository.findAirplanesByDateOrderByPriceAsc(
                    departureAirport, arrivalAirport, departureDate
            );
        }
    }

    @Transactional
    public Reserve makeReservation(String cno, String flightNo, LocalDateTime departureDateTime, String seatClass) {

        // 1. 고객 정보 조회
        Customer customer = customerRepository.findById(cno)
                .orElseThrow(() -> new IllegalArgumentException("해당 고객을 찾을 수 없습니다. cno=" + cno));

        // 2. 예약할 좌석(Seats) 정보 조회 (Airplane이 아닌 Seats를 조회)
        //    - Seats의 복합 키(SeatsId)를 먼저 만들어야 함
        SeatsPK seatsPK = new SeatsPK(flightNo, departureDateTime, seatClass);
        Seats seats = seatsRepository.findById(seatsPK)
                .orElseThrow(() -> new IllegalArgumentException("해당 좌석 정보를 찾을 수 없습니다."));

        // 3. 좌석 재고 확인 및 차감 로직 (핵심 변경 부분)
        // ----------------------------------------------------
        // 현재 좌석 수를 가져옵니다.
        int currentSeats = seats.getNo_of_seats();

        // 재고가 0 이하인지 확인합니다.
        if (currentSeats <= 0) {
            throw new IllegalStateException("남은 좌석이 없습니다.");
        }

        // 재고가 있다면 1을 감소시킨 값을 setter로 설정합니다.
        seats.setNo_of_seats(currentSeats - 1);


        // 3. 저장할 Reserve 엔티티 객체를 빌더로 생성
        //    수정된 엔티티의 모든 필드에 값을 채워줍니다.
        Reserve newReserve = Reserve.builder()
                // --- 복합 키(PK)를 구성하는 필드들 ---
                // customer 객체에서 PK인 cno 값을 꺼내서 설정
                .customer(customer.getCno())
                // seats 객체에서 PK를 구성하는 각 값을 꺼내서 설정
                .flightNo(seats.getFlightNo())
                .departureDateTime(seats.getDepartureDateTime())
                .seatClass(seats.getSeatClass())

                // --- 관계(조회용) 필드들 ---
                // 실제 Customer와 Seats 객체를 관계 필드에 설정
                .customerRel(customer)
                .seatsRel(seats)

                // --- 일반 데이터 필드들 ---
                .payment(seats.getPrice()) // 좌석 정보에 있는 가격을 결제 금액으로 설정
                .reserveDateTime(LocalDateTime.now()) // 예약 시점은 현재 시간으로

                .build();

        // 1. 예약을 DB에 먼저 저장하고, 저장된 객체를 받음
        Reserve savedReserve = reserveRepository.save(newReserve);

        // --- 여기가 핵심 변경 부분 ---
        // 1. 저장된 엔티티에서 DTO에 필요한 모든 정보를 미리 추출합니다.
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

        // 2. 엔티티 대신, 정보가 모두 담긴 DTO를 이메일 서비스에 전달합니다.
        emailService.sendBoardingPass(boardingPassInfo);
        // -------------------------

        // 3. 저장된 예약 객체를 반환
        return savedReserve;
    }

    public Reserve getReservation(String cno, String flightNo, LocalDateTime departureDateTime, String seatClass) {
        ReservePK reservePK = new ReservePK(cno, flightNo, departureDateTime, seatClass);
        return reserveRepository.findById(reservePK)
                .orElseThrow(() -> new IllegalArgumentException("해당 예약을 찾을 수 없습니다."));
    }

    // getMyActiveReservations 메소드 수정
    public List<Reserve> getMyReservations(String cno, LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null) {
            // 날짜 범위가 지정된 경우
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay().minusNanos(1);
            return reserveRepository.findActiveReservationsByCustomerCnoAndDateRange(cno, startDateTime, endDateTime);
        } else {
            // 날짜 범위가 없는 경우 (기존 로직)
            return reserveRepository.findActiveReservationsByCustomerCno(cno);
        }
    }

    /**
     * 예약을 취소하고, 취소 내역을 생성합니다.
     */
    @Transactional
    public Cancel cancelReservation(String cno, String flightNo, LocalDateTime departureDateTime, String seatClass) {
        // --- 1. 취소할 예약이 실제로 존재하는지 확인 ---
        ReservePK reservePK = new ReservePK(cno, flightNo, departureDateTime, seatClass);
        Reserve reserveToCancel = reserveRepository.findById(reservePK)
                .orElseThrow(() -> new IllegalArgumentException("취소할 예약 정보를 찾을 수 없습니다."));

        // --- 2. 이미 취소된 예약인지 확인 ---
        CancelPK cancelPK = new CancelPK(cno, flightNo, departureDateTime, seatClass);
        if (cancelRepository.existsById(cancelPK)) {
            throw new IllegalStateException("이미 취소된 예약입니다.");
        }

        // --- 2. 환불 금액 계산 로직 (핵심 수정 부분) ---
        // ----------------------------------------------------
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
            // 당일 또는 날짜가 지난 경우 (daysUntilDeparture <= 0)
            penaltyFee = payment; // 전액 위약금
        }

        // 최종 환불액 계산 (환불액이 음수가 되지 않도록 처리)
        int finalRefundAmount = Math.max(0, payment - penaltyFee);
        // ----------------------------------------------------


        // --- 3. 좌석 수(재고)를 1 증가시킴 ---
        Seats seats = reserveToCancel.getSeatsRel();
        seats.setNo_of_seats(seats.getNo_of_seats() + 1);
        seatsRepository.save(seats); // 변경된 좌석 정보를 DB에 반영

        // --- 4. Cancel 엔티티를 생성하여 DB에 저장 ---
        Cancel newCancel = Cancel.builder()
                // 복합 키(PK) 필드 설정
                .customer(reserveToCancel.getCustomer())
                .flightNo(reserveToCancel.getFlightNo())
                .departureDateTime(reserveToCancel.getDepartureDateTime())
                .seatClass(reserveToCancel.getSeatClass())
                // 관계(조회용) 필드 설정
                .customerRel(reserveToCancel.getCustomerRel())
                .seatsRel(reserveToCancel.getSeatsRel())
                // 일반 데이터 필드 설정
                .refund(finalRefundAmount)
                .cancelDateTime(LocalDateTime.now())
                .build();

        return cancelRepository.save(newCancel);
    }

    /**
     * 특정 취소 내역 한 건을 조회합니다.
     */
    public Cancel getCancellation(String cno, String flightNo, LocalDateTime departureDateTime, String seatClass) {
        CancelPK cancelId = new CancelPK(cno, flightNo, departureDateTime, seatClass);
        return cancelRepository.findById(cancelId)
                .orElseThrow(() -> new IllegalArgumentException("해당 취소 내역을 찾을 수 없습니다."));
    }

    // getMyCancellations 메소드 수정
    public List<Cancel> getMyCancellations(String cno, LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null) {
            // 날짜 범위가 지정된 경우
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay().minusNanos(1);
            return cancelRepository.findByCustomerAndCancelDateTimeBetween(cno, startDateTime, endDateTime);
        } else {
            // 날짜 범위가 없는 경우 (기존 로직)
            return cancelRepository.findByCustomer(cno);
        }
    }

    public List<ReservationStats> getReservationStatistics() {
        return reserveRepository.findReservationStats();
    }

    public List<CancellationStats> getCancellationStatistics() {
        return cancelRepository.findCancellationStats();
    }

}
