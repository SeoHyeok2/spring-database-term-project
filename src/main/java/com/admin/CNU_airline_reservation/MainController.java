package com.admin.CNU_airline_reservation;

import com.admin.CNU_airline_reservation.dto.LoginRequest;
import com.admin.CNU_airline_reservation.entity.Airplane;
import com.admin.CNU_airline_reservation.entity.Customer;
import com.admin.CNU_airline_reservation.entity.Reserve;
import com.admin.CNU_airline_reservation.entity.SeatsPK;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final MainService mainService;

    /** 메인페이지 */
    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        // 로그인 상태 처리
        String cno = (String) session.getAttribute("cno");
        if (cno != null) {
            model.addAttribute("cno", cno);
            model.addAttribute("name", session.getAttribute("name"));
        }
        
        // 전체 항공편 목록만 조회
        List<Airplane> airplanes = mainService.findAllAirplanes();
        model.addAttribute("airplanes", airplanes);
        
        return "index";
    }

    /** 로그인 폼 */
    @GetMapping("/members/login")
    public String showLoginForm() {
        return "login";
    }

    /** 로그인 처리 */
    @PostMapping("/members/login")
    public String processLogin(@ModelAttribute LoginRequest loginRequest,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) { // 1. RedirectAttributes 추가

        // 2. 서비스 메소드가 Optional<Customer>를 반환한다고 가정
        Optional<Customer> loginResult = mainService.login(loginRequest.cno(), loginRequest.password());

        // 3. 로그인 성공/실패 여부 확인
        if (loginResult.isPresent()) {
            // 4. 로그인 성공 시
            Customer customer = loginResult.get();
            session.setAttribute("cno", customer.getCno());
            session.setAttribute("name", customer.getName());
            return "redirect:/"; // 메인 페이지로 이동

        } else {
            // 5. 로그인 실패 시
            // ?error=true 파라미터를 붙여서 리다이렉트
            redirectAttributes.addAttribute("error", true);
            return "redirect:/members/login";
        }
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
                                  @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate departureDate,
                                  @RequestParam(name = "sort", required = false, defaultValue = "time") String sortOption) {
        List<Airplane> airplanes = mainService.searchAirplanes(departureAirport, arrivalAirport, departureDate, sortOption);
        model.addAttribute("airplanes", airplanes);
        model.addAttribute("formattedDate", 
            departureDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")));
        return "search_results";
    }

    /** 예약 생성 */
    @PostMapping("/reservations")
    public String makeReservation(@RequestParam String flightNo,
                                  @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime departureDateTime,
                                  @RequestParam String seatClass,
                                  HttpSession session) {
        String cno = (String) session.getAttribute("cno");
        if (cno == null) {
            return "redirect:/members/login";
        }

//        Reserve reserve = mainService.makeReservation(cno, flightNo, departureDateTime, seatClass);
        return "redirect:/reservations/success?reservationId=" + cno;
    }

    /** 예약 완료 페이지 */
    @GetMapping("/reservations/success")
    public String showReservationSuccess(@RequestParam String cno,
                                         @RequestParam String flightNo,
                                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime departureDateTime,
                                         @RequestParam String seatClass,
                                         Model model) {
        Reserve reserve = mainService.getReservation(cno, flightNo, departureDateTime, seatClass);
        model.addAttribute("reserve", reserve);
        return "reservation_success";
    }

    /** 내 예약 목록 조회 */
    @GetMapping("/reservations/my")
    public String viewMyReservations(Model model, HttpSession session) {
        String cno = (String) session.getAttribute("cno");
        if (cno == null) {
            return "redirect:/members/login";
        }
        
        List<Reserve> reserves = mainService.getMyReservations(cno);
        model.addAttribute("reserves", reserves);
        return "my_reservations";
    }

//    /** 예약 취소 */
//    @PostMapping("/reservations/{reservationId}/cancel")
//    public String cancelReservation(@PathVariable Long reservationId, HttpSession session) {
//        Long memberId = (Long) session.getAttribute("memberId");
//        if (memberId == null) {
//            return "redirect:/members/login";
//        }
//
//        mainService.cancelReservation(reservationId, memberId);
//        return "redirect:/reservations/my";
//    }
}
