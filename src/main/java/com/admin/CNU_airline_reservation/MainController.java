package com.admin.CNU_airline_reservation;

import com.admin.CNU_airline_reservation.dto.CancellationStats;
import com.admin.CNU_airline_reservation.dto.LoginRequest;
import com.admin.CNU_airline_reservation.dto.ReservationStats;
import com.admin.CNU_airline_reservation.entity.*;
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

/**
 * 웹 애플리케이션의 주요 HTTP 요청을 처리하는 중앙 컨트롤러
 * 사용자, 항공편, 예약, 통계 등 다양한 도메인의 요청을 받아 서비스 계층에 처리를 위임
 */
@Controller
@RequiredArgsConstructor
public class MainController {

    private final MainService mainService;

    /**
     * 메인 페이지를 표시
     */
    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        // 로그인 상태 처리
        String cno = (String) session.getAttribute("cno");
        if (cno != null) {
            model.addAttribute("cno", cno);
            model.addAttribute("name", session.getAttribute("name"));
        }
        
        // 전체 항공편 목록 조회
        List<Airplane> airplanes = mainService.findAllAirplanes();
        model.addAttribute("airplanes", airplanes);
        
        return "index";
    }

    // ========== 회원 관련 기능 ==========

    /**
     * 로그인 폼 페이지 표시
     */
    @GetMapping("/members/login")
    public String showLoginForm() {
        return "login";
    }

    /**
     * 로그인을 처리
     */
    @PostMapping("/members/login")
    public String processLogin(@ModelAttribute LoginRequest loginRequest,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {

        Optional<Customer> loginResult = mainService.login(loginRequest.cno(), loginRequest.password());

        if (loginResult.isPresent()) {
            // 로그인 성공 시
            Customer customer = loginResult.get();
            session.setAttribute("cno", customer.getCno());
            session.setAttribute("name", customer.getName());
            return "redirect:/"; // 메인 페이지로 이동

        } else {
            // 로그인 실패 시
            redirectAttributes.addAttribute("error", true);
            return "redirect:/members/login"; // ?error=true 파라미터를 붙여서 리다이렉트
        }
    }

    /**
     * 로그아웃을 처리
     */
    @GetMapping("/members/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    // ========== 항공편 검색 기능 ==========

    /**
     * 항공편 검색 결과를 표시
     */
    @GetMapping("/flights/search/results")
    public String showSearchResults(Model model,
                                  @RequestParam String departureAirport,
                                  @RequestParam String arrivalAirport,
                                  @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate departureDate,
                                  @RequestParam String seatClass,
                                  @RequestParam(name = "sort", required = false, defaultValue = "time") String sortOption) {
        List<Airplane> airplanes = mainService.searchAirplanes(departureAirport, arrivalAirport, departureDate, seatClass, sortOption);
        model.addAttribute("airplanes", airplanes);
        model.addAttribute("formattedDate", 
            departureDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")));
        model.addAttribute("seatClass", seatClass);
        return "search_results";
    }

    // ========== 예약 및 취소 기능 ==========

    /**
     * 예약을 생성
     */
    @PostMapping("/reservations")
    public String makeReservation(@RequestParam String flightNo,
                                  @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime departureDateTime,
                                  @RequestParam String seatClass,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {

        String cno = (String) session.getAttribute("cno");
        if (cno == null) {
            return "redirect:/members/login";
        }

        try {
            Reserve savedReserve = mainService.makeReservation(cno, flightNo, departureDateTime, seatClass);
            redirectAttributes.addAttribute("flightNo", savedReserve.getFlightNo());
            redirectAttributes.addAttribute("departureDateTime", savedReserve.getDepartureDateTime().toString());
            redirectAttributes.addAttribute("seatClass", savedReserve.getSeatClass());
            redirectAttributes.addAttribute("cno", cno);

            return "redirect:/reservations/success"; // 성공 시 예약 완료 페이지로 리다이렉트
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "예약에 실패했습니다: " + e.getMessage());
            return "redirect:/"; // 실패 시 메인 페이지로 리다이렉트
        }
    }

    /**
     * 특정 예약의 완료 정보를 보여주는 페이지 표시
     */
    @GetMapping("/reservations/success")
    public String showReservationSuccess(
            @RequestParam String cno,
            @RequestParam String flightNo,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime departureDateTime,
            @RequestParam String seatClass,
            Model model) {

        Reserve reserve = mainService.getReservation(cno, flightNo, departureDateTime, seatClass);
        model.addAttribute("reserve", reserve);

        return "reservation_success";
    }

    /**
     * 예약을 취소
     */
    @PostMapping("/reservations/cancel")
    public String processCancellation(@RequestParam String flightNo,
                                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime departureDateTime,
                                      @RequestParam String seatClass,
                                      HttpSession session,
                                      RedirectAttributes redirectAttributes) {
        String cno = (String) session.getAttribute("cno");
        if (cno == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
            return "redirect:/members/login";
        }

        try {
            Cancel cancelled = mainService.cancelReservation(cno, flightNo, departureDateTime, seatClass);

            redirectAttributes.addAttribute("flightNo", cancelled.getFlightNo());
            redirectAttributes.addAttribute("departureDateTime", cancelled.getDepartureDateTime().toString());
            redirectAttributes.addAttribute("seatClass", cancelled.getSeatClass());
            redirectAttributes.addAttribute("cno", cno);

            return "redirect:/reservations/cancel/success"; // 성공 시 취소 완료 페이지로 리다이렉트
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "예약 취소에 실패했습니다: " + e.getMessage());
            return "redirect:/reservations/my"; // 실패 시 내 예약 내역으로 리다이렉트
        }
    }

    /**
     * 특정 예약의 취소 완료 정보를 보여주는 페이지 표시
     */
    @GetMapping("/reservations/cancel/success")
    public String showCancellationSuccess(@RequestParam String cno,
                                          @RequestParam String flightNo,
                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime departureDateTime,
                                          @RequestParam String seatClass,
                                          Model model) {
        Cancel cancel = mainService.getCancellation(cno, flightNo, departureDateTime, seatClass);
        model.addAttribute("cancel", cancel);

        return "cancellation_success";
    }

    /**
     * '마이 페이지'를 표시
     * 사용자의 활성 예약 목록과 취소 내역 목록을 모두 조회
     * 날짜 및 탭 필터링 기능을 포함
     */
    @GetMapping("/members/my_page")
    public String viewMyPage(Model model,
                             HttpSession session,
                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                             @RequestParam(required = false, defaultValue = "all") String filterType) {
        String cno = (String) session.getAttribute("cno");
        if (cno == null) {
            return "redirect:/members/login";
        }

        List<Reserve> reserves = mainService.getMyReservations(cno, startDate, endDate);
        List<Cancel> cancels = mainService.getMyCancellations(cno, startDate, endDate);

        model.addAttribute("reserves", reserves);
        model.addAttribute("cancels", cancels);

        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        model.addAttribute("filterType", filterType);

        return "my_page";
    }

    // ========== 관리자 통계 기능 ==========

    /**
     * 예약 통계 페이지를 표시
     */
    @GetMapping("/stats/reservations")
    public String showReservationStats(Model model) {
        List<ReservationStats> stats = mainService.getReservationStatistics();
        model.addAttribute("reservationStats", stats);

        // 활성 탭 표시용
        model.addAttribute("activeStat", "reservations");

        return "admin/reservation_stats";
    }

    /**
     * 취소 통계 페이지를 표시
     */
    @GetMapping("/stats/cancellations")
    public String showCancellationStats(Model model) {
        List<CancellationStats> stats = mainService.getCancellationStatistics();
        model.addAttribute("cancellationStats", stats);

        // 활성 탭 표시용
        model.addAttribute("activeStat", "cancellations");

        return "admin/cancellation_stats";
    }
}
