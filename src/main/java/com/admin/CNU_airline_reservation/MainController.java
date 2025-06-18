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
            // 1. 예약을 생성합니다.
            Reserve savedReserve = mainService.makeReservation(cno, flightNo, departureDateTime, seatClass);

            // 2. 성공 페이지로 리다이렉트할 URL을 만듭니다.
            //    방금 만든 예약의 복합 키 정보들을 URL 파라미터로 모두 넘겨줍니다.
            redirectAttributes.addAttribute("flightNo", savedReserve.getFlightNo());
            redirectAttributes.addAttribute("departureDateTime", savedReserve.getDepartureDateTime().toString());
            redirectAttributes.addAttribute("seatClass", savedReserve.getSeatClass());
            // cno는 세션에 있으므로 굳이 넘길 필요는 없지만, 명확성을 위해 함께 넘겨줍니다.
            redirectAttributes.addAttribute("cno", cno);

            return "redirect:/reservations/success";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "예약에 실패했습니다: " + e.getMessage());
            return "redirect:/"; // 에러 발생 시 메인 페이지로
        }
    }
    
    /**
     * 예약 완료 페이지 조회 (GET 방식)
     */
    @GetMapping("/reservations/success")
    public String showReservationSuccess(
            // 3. 리다이렉트된 URL의 파라미터들을 받습니다.
            @RequestParam String cno,
            @RequestParam String flightNo,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime departureDateTime,
            @RequestParam String seatClass,
            Model model) {

        // 4. 받은 키 값들로 예약 정보를 다시 조회합니다.
        Reserve reserve = mainService.getReservation(cno, flightNo, departureDateTime, seatClass);
        model.addAttribute("reserve", reserve);

        return "reservation_success"; // templates/reservation_success.html
    }

    /**
     * 예약을 취소하고, Cancel 테이블에 기록을 생성합니다.
     */
    @PostMapping("/reservations/cancel")
    public String processCancellation(@RequestParam String flightNo,
                                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime departureDateTime,
                                      @RequestParam String seatClass,
                                      HttpSession session,
                                      RedirectAttributes redirectAttributes) {
        // 1. 세션에서 현재 로그인한 사용자 정보를 가져옵니다.
        String cno = (String) session.getAttribute("cno");
        if (cno == null) {
            // 비로그인 사용자는 로그인 페이지로 보냅니다.
            redirectAttributes.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
            return "redirect:/members/login";
        }

        try {
            // 2. 서비스에 취소 처리를 위임합니다.
            Cancel cancelled = mainService.cancelReservation(cno, flightNo, departureDateTime, seatClass);

            // 3. 성공 시, 취소 완료 페이지로 리다이렉트하며 식별 정보를 넘겨줍니다.
            redirectAttributes.addAttribute("flightNo", cancelled.getFlightNo());
            redirectAttributes.addAttribute("departureDateTime", cancelled.getDepartureDateTime().toString());
            redirectAttributes.addAttribute("seatClass", cancelled.getSeatClass());
            redirectAttributes.addAttribute("cno", cno);

            return "redirect:/reservations/cancel/success";

        } catch (Exception e) {
            // 이미 취소된 예약이거나, 취소할 예약이 없는 경우 등
            redirectAttributes.addFlashAttribute("errorMessage", "예약 취소에 실패했습니다: " + e.getMessage());
            return "redirect:/reservations/my"; // 내 예약 목록으로 되돌아감
        }
    }

    /**
     * 예약 취소 완료 페이지를 보여줍니다.
     */
    @GetMapping("/reservations/cancel/success")
    public String showCancellationSuccess(@RequestParam String cno,
                                          @RequestParam String flightNo,
                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime departureDateTime,
                                          @RequestParam String seatClass,
                                          Model model) {
        // URL에 담겨온 키 값들로 특정 취소 내역을 다시 조회합니다.
        Cancel cancel = mainService.getCancellation(cno, flightNo, departureDateTime, seatClass);
        model.addAttribute("cancel", cancel);

        return "cancellation_success"; // templates/cancellation_success.html
    }

    /**
     * 내 취소 내역 목록을 보여줍니다.
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
        // 서비스로부터 특정 고객의 모든 취소 내역을 받아옵니다.
        List<Cancel> cancels = mainService.getMyCancellations(cno, startDate, endDate);

        model.addAttribute("reserves", reserves);
        model.addAttribute("cancels", cancels);

        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        model.addAttribute("filterType", filterType);

        return "my_page"; // templates/my_page.html
    }

    @GetMapping("/stats/reservations")
    public String showReservationStats(Model model) {
        List<ReservationStats> stats = mainService.getReservationStatistics();
        model.addAttribute("reservationStats", stats);

        // 이 페이지가 '예약 통계'임을 View에 알려줍니다.
        model.addAttribute("activeStat", "reservations");

        return "admin/reservation_stats"; // templates/admin/stats_reservation.html
    }

    @GetMapping("/stats/cancellations")
    public String showCancellationStats(Model model) {
        List<CancellationStats> stats = mainService.getCancellationStatistics();
        model.addAttribute("cancellationStats", stats);

        // 이 페이지가 '취소 통계'임을 View에 알려줍니다.
        model.addAttribute("activeStat", "cancellations");

        return "admin/cancellation_stats"; // templates/admin/stats_reservation.html
    }
}
