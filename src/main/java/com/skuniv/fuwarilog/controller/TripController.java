package com.skuniv.fuwarilog.controller;

import com.skuniv.fuwarilog.config.exception.BadRequestException;
import com.skuniv.fuwarilog.config.exception.ErrorResponseStatus;
import com.skuniv.fuwarilog.domain.User;
import com.skuniv.fuwarilog.dto.Trip.TripRequest;
import com.skuniv.fuwarilog.dto.Trip.TripResponse;
import com.skuniv.fuwarilog.repository.UserRepository;
import com.skuniv.fuwarilog.security.jwt.JwtTokenProvider;
import com.skuniv.fuwarilog.service.TripService;
import com.skuniv.fuwarilog.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Calendar(Trip) API", description = "여행일정 관련(캘린더) 조회, 수정, 삭제")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/trips")
public class TripController {

    private final TripService tripService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @GetMapping("/event/month")
    @Operation(summary = "월별 여행 일정 조회 API", description = "연도, 월 입력시 해당하는 기간의 여행일정 반환")
    public ResponseEntity<List<TripResponse.TripListDTO>> getEventsByMonth (
            Authentication authentication,
            @RequestParam int year,
            @RequestParam int month) {

        String email = (String) authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.USER_NOT_FOUND));

        List<TripResponse.TripListDTO> result = tripService.getEventsByMonth(user.getId(), year, month);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/event/today")
    @Operation(summary = "현재 여행 일정 목록 조회 API", description = "오늘 날짜에 해당하는 여행일정 반환")
    public ResponseEntity<List<TripResponse.TripListDTO>> getEventsByToday (
            Authentication authentication) {

        String email = (String) authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.USER_NOT_FOUND));

        List<TripResponse.TripListDTO> result = tripService.getEventsByToday(user.getId());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/event/schedule")
    @Operation(summary = "일주일 뒤의 예정된 일정 목록 조회 API", description = "오늘 날짜로부터 일주일 뒤의 여행일정 최대 3개 반환")
    public ResponseEntity<List<TripResponse.TripListDTO>> getEventsByNextWeek (
            Authentication authentication) {

        String email = (String) authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.USER_NOT_FOUND));

        List<TripResponse.TripListDTO> result = tripService.getEventsByNextWeek(user.getId());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/event/{id}")
    @Operation(summary = "특정 일정 상세 조회 API", description = "tripId 입력시 해당 여행일정 상세 정보 조회")
    public ResponseEntity<List<TripResponse.TripInfoDTO>> getEvent(
            Authentication authentication,
            @PathVariable(required = true) Long id) {

        String email = (String) authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.USER_NOT_FOUND));

        List<TripResponse.TripInfoDTO> events = tripService.getEvents(user.getId(), id);
        return ResponseEntity.ok(events);
    }

    @PostMapping("/event")
    @Operation(summary = "일정 추가 API", description = "제목, 설명, 시작일, 마지막일 입력하면 일정 ID 반환 - 다이어리 자동 생성")
    public ResponseEntity<TripResponse.TripInfoDTO> createTrip(
            Authentication authentication,
            @RequestBody TripRequest.TripInfoDTO event) {

        String email = (String) authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.USER_NOT_FOUND));

        // 3. 구글캘린더 일정 -> 여행 일정
        try {
            TripResponse.TripInfoDTO result = tripService.createEvent(
                    email,
                    event.getTitle(),
                    event.getDescription(),
                    event.getStartDate().toString(),
                    event.getEndDate().toString(),
                    event.getCountry()
            );
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/event/{tripId}")
    @Operation(summary = "여행일정 수정 API", description = " 여행ID 입력 시 해당 일정 수정 가능")
    public ResponseEntity<TripResponse.TripInfoDTO> editEvent(
            Authentication authentication,
            @PathVariable Long tripId,
            @RequestBody TripRequest.TripInfoDTO infoDTO) {

        String email = (String) authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.USER_NOT_FOUND));
        TripResponse.TripInfoDTO result = tripService.editEvent(user.getId(), tripId, infoDTO);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/event/{tripId}")
    @Operation(summary = "일정 삭제 API", description = "Trip id 입력 시 해당하는 여행일정 삭제")
    public ResponseEntity<Void> deleteTrip(
            Authentication authentication,
            @PathVariable Long tripId) {

        String email = (String) authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.USER_NOT_FOUND));

        try {
            tripService.deleteEvent(email, tripId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}

