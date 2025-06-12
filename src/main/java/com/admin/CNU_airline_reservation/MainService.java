package com.admin.CNU_airline_reservation;


import com.admin.CNU_airline_reservation.entity.*;
import com.admin.CNU_airline_reservation.repository.AirplaneRepository;
import com.admin.CNU_airline_reservation.repository.CustomerRepository;
import com.admin.CNU_airline_reservation.repository.ReserveRepository;
import com.admin.CNU_airline_reservation.repository.SeatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MainService {
    private final AirplaneRepository airplaneRepository;
    private final CustomerRepository customerRepository;
    private final ReserveRepository reserveRepository;
    private final SeatsRepository seatsRepository;

    public Customer login(String email, String password) {
        Optional<Customer> customer = customerRepository.findByEmailAndPasswd(email, password);
        if(customer.isEmpty()){
            throw new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }
        return customer.get();
    }

    public List<Airplane> searchAirplanes(String departureAirport, String arrivalAirport, LocalDate departureDate) {

        // 1. 넘어온 LocalDate로 '오늘의 시작' 시간 만들기
        // 예: departureDate가 2025-06-13 이라면 -> 2025-06-13T00:00:00
        LocalDateTime startOfDay = departureDate.atStartOfDay();

        // 2. 넘어온 LocalDate로 '오늘의 끝' 시간 만들기
        // 예: departureDate가 2025-06-13 이라면 -> 2025-06-13T23:59:59.999...
        LocalDateTime endOfDay = departureDate.plusDays(1).atStartOfDay().minusNanos(1);

        // 3. 가공한 시작 시간과 끝 시간을 전달하여 리포지토리 메소드 호출
        return airplaneRepository.findAirplanesWithSeatsByConditions(
                departureAirport,
                arrivalAirport,
                startOfDay,
                endOfDay
        );
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

        // (추가 로직: 해당 좌석의 재고(no_of_seats)가 남아있는지 확인하는 로직이 필요할 수 있습니다.)

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

        // 4. JpaRepository의 save() 메소드에는 엔티티 객체 하나만 전달
        return reserveRepository.save(newReserve);
    }

    public Reserve getReservation(String cno, String flightNo, LocalDateTime departureDateTime, String seatClass) {
        ReservePK reservePK = new ReservePK(cno, flightNo, departureDateTime, seatClass);
        return reserveRepository.findById(reservePK)
                .orElseThrow(() -> new IllegalArgumentException("해당 예약을 찾을 수 없습니다."));
    }

    public List<Reserve> getMyReservations(String cno) {
        return reserveRepository.findByCustomerCnoWithDetails(cno);
    }

    public List<Airplane> findAllAirplanes() {
        return airplaneRepository.findAllWithSeatsOrderByDepartureDateTimeAsc();
    }

//    @Transactional
//    public void cancelReservation(Long reservationId, Long memberId) {
//        Reserve reserve = reserveRepository.findByIdAndMemberId(reservationId, memberId);
//        if (reserve == null) {
//            throw new IllegalArgumentException("예약을 찾을 수 없거나 취소 권한이 없습니다.");
//        }
//
//        reserveRepository.deleteReservation(reservationId, memberId);
//    }
}
