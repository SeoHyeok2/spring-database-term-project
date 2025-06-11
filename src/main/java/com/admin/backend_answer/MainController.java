package com.admin.backend_answer;

import com.admin.backend_answer.dto.LoginRequest;
import com.admin.backend_answer.dto.SignupRequest;
import com.admin.backend_answer.entity.Member;
import com.admin.backend_answer.entity.Flight;
import com.admin.backend_answer.entity.Reservation;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import org.springframework.ui.Model;
import java.util.List;
import java.time.format.DateTimeFormatter;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final MainService mainService;

    /** 메인페이지 */
    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        // 로그인 상태 처리
        Long memberId = (Long) session.getAttribute("memberId");
        if (memberId != null) {
            model.addAttribute("memberId", memberId);
            model.addAttribute("memberName", session.getAttribute("memberName"));
        }
        
        // 전체 항공편 목록만 조회
        List<Flight> flights = mainService.findAllFlights();
        model.addAttribute("flights", flights);
        
        return "index";
    }

    /** 회원가입 폼 */
    @GetMapping("/members/signup")
    public String showSignupForm() {
        return "signup";
    }

    /** 회원가입 처리 */
    @PostMapping("/members/signup")
    public String processSignup(@ModelAttribute SignupRequest signupRequest) {
        String memberName = signupRequest.memberName();
        String memberEmail = signupRequest.memberEmail();
        String memberPassword = signupRequest.memberPassword();
        String passportNumber = signupRequest.passportNumber();
        mainService.signup(memberName, memberEmail, memberPassword, passportNumber);
        return "redirect:/members/login";
    }

    /** 로그인 폼 */
    @GetMapping("/members/login")
    public String showLoginForm() {
        return "login";
    }

    /** 로그인 처리 */
    @PostMapping("/members/login")
    public String processLogin(@ModelAttribute LoginRequest loginRequest,
                               HttpSession session) {
        String email = loginRequest.email();
        String password = loginRequest.password();
        Member member = mainService.login(email, password);
        session.setAttribute("memberId", member.getMemberId());
        session.setAttribute("memberName", member.getMemberName());
        return "redirect:/";
    }

    /** 로그아웃 처리 */
    @GetMapping("/members/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
    
    /** 검색 결과 표시 */
    @GetMapping("/flights/search/results")
    public String showSearchResults(Model model,
                                  @RequestParam String departureAirport,
                                  @RequestParam String arrivalAirport,
                                  @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate departureDate) {
        List<Flight> flights = mainService.searchFlights(departureAirport, arrivalAirport, departureDate);
        model.addAttribute("flights", flights);
        model.addAttribute("formattedDate", 
            departureDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")));
        return "search_results";
    }

    /** 예약 생성 */
    @PostMapping("/reservations")
    public String makeReservation(@RequestParam Long flightId,
                                HttpSession session) {
        Long memberId = (Long) session.getAttribute("memberId");
        if (memberId == null) {
            return "redirect:/members/login";
        }
        
        Reservation reservation = mainService.makeReservation(memberId, flightId);
        return "redirect:/reservations/success?reservationId=" + reservation.getReservationId();
    }

    /** 예약 완료 페이지 */
    @GetMapping("/reservations/success")
    public String showReservationSuccess(@RequestParam Long reservationId, Model model) {
        Reservation reservation = mainService.getReservation(reservationId);
        model.addAttribute("reservation", reservation);
        return "reservation_success";
    }

    /** 내 예약 목록 조회 */
    @GetMapping("/reservations/my")
    public String viewMyReservations(Model model, HttpSession session) {
        Long memberId = (Long) session.getAttribute("memberId");
        if (memberId == null) {
            return "redirect:/members/login";
        }
        
        List<Reservation> reservations = mainService.getMyReservations(memberId);
        model.addAttribute("reservations", reservations);
        return "my_reservations";
    }

    /** 예약 취소 */
    @PostMapping("/reservations/{reservationId}/cancel")
    public String cancelReservation(@PathVariable Long reservationId, HttpSession session) {
        Long memberId = (Long) session.getAttribute("memberId");
        if (memberId == null) {
            return "redirect:/members/login";
        }
        
        mainService.cancelReservation(reservationId, memberId);
        return "redirect:/reservations/my";
    }
}
