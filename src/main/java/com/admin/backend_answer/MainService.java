package com.admin.backend_answer;

import com.admin.backend_answer.repository.FlightRepository;
import com.admin.backend_answer.repository.MemberRepository;
import com.admin.backend_answer.repository.ReservationRepository;
import com.admin.backend_answer.entity.Member;
import com.admin.backend_answer.entity.Flight;
import com.admin.backend_answer.entity.Reservation;
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
    private final FlightRepository flightRepository;
    private final MemberRepository memberRepository;
    private final ReservationRepository reservationRepository;

    @Transactional
    public void signup(String memberName, String memberEmail, String memberPassword, String passportNumber) {
        memberRepository.saveMember(memberName, memberEmail, memberPassword, passportNumber);
    }

    public Member login(String email, String password) {
        Optional<Member> member = memberRepository.findByMemberEmailAndMemberPassword(email, password);
        if(member.isEmpty()){
            throw new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }
        return member.get();
    }

    public List<Flight> searchFlights(String departureAirport, String arrivalAirport, LocalDate departureDate) {
        return flightRepository.findByDepartureAirportAndArrivalAirportAndDepartureDate(
                departureAirport, arrivalAirport, departureDate);
    }

    @Transactional
    public Reservation makeReservation(Long memberId, Long flightId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new IllegalArgumentException("Flight not found"));

        reservationRepository.saveReservation(
            member.getMemberId(),
            flight.getFlightId(),
            flight.getFee(),
            LocalDateTime.now()
        );

        // 저장된 예약 정보를 조회하여 반환
        return reservationRepository.findLatestReservation(memberId, flightId);
    }

    public Reservation getReservation(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
    }

    public List<Reservation> getMyReservations(Long memberId) {
        return reservationRepository.findByMemberMemberId(memberId);
    }

    public List<Flight> findAllFlights() {
        return flightRepository.findAllOrderByDepartureTime();
    }

    @Transactional
    public void cancelReservation(Long reservationId, Long memberId) {
        Reservation reservation = reservationRepository.findByIdAndMemberId(reservationId, memberId);
        if (reservation == null) {
            throw new IllegalArgumentException("예약을 찾을 수 없거나 취소 권한이 없습니다.");
        }
        
        reservationRepository.deleteReservation(reservationId, memberId);
    }
}
