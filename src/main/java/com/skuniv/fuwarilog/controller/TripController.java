package com.skuniv.fuwarilog.controller;

import com.skuniv.fuwarilog.config.exception.BadRequestException;
import com.skuniv.fuwarilog.config.exception.ErrorResponseStatus;
import com.skuniv.fuwarilog.dto.TripRequest;
import com.skuniv.fuwarilog.dto.TripResponse;
import com.skuniv.fuwarilog.security.jwt.JwtTokenProvider;
import com.skuniv.fuwarilog.service.TripService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name = "Calendar(Trip) API", description = "여행일정 관련(캘린더) 조회, 수정, 삭제")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/trips")
public class TripController {

    private final TripService tripService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/event")
    @Operation(summary = "일정 추가 API", description = "제목, 설명, 시작일, 마지막일 입력하면 일정 ID 반환 - 다이어리 자동 생성")
    public ResponseEntity<TripResponse.TripInfoDTO> createTrip(
            @RequestHeader("Authorization") String token,
            @RequestBody TripRequest.TripInfoDTO event) {

        // 1. 토큰 확인
        if(!jwtTokenProvider.validateToken(token)) { throw new BadRequestException(ErrorResponseStatus.INVALID_TOKEN); }

        // 2. 사용자 이메일 추출
        String userEmail = jwtTokenProvider.getUserEmail(token);

        // 3. 구글캘린더 일정 -> 여행 일정
        try {
            TripResponse.TripInfoDTO result = tripService.createEvent(
                    userEmail,
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

    @PostMapping("/event/{id}")
    @Operation(summary = "여행일정 수정 API", description = " 여행ID 입력 시 해당 일정 수정 가능")
    public ResponseEntity<TripResponse.TripInfoDTO> editEvent(
            @RequestHeader("Authorization") String token,
            @PathVariable Long tripId,
            @RequestBody TripRequest.TripInfoDTO infoDTO) {

        // 1. 토큰 확인
        if(!jwtTokenProvider.validateToken(token)) { throw new BadRequestException(ErrorResponseStatus.INVALID_TOKEN); }

        // 2. 토큰 -> 사용자 아이디
        Long userId = jwtTokenProvider.getUserId(token);
        TripResponse.TripInfoDTO result = tripService.editEvent(userId, tripId, infoDTO);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/event/{id}")
    @Operation(summary = "일정 삭제 API", description = "Trip id 입력 시 해당하는 여행일정 삭제")
    public ResponseEntity<Void> deleteTrip(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id) {

        // 1. 토큰 확인
        if(!jwtTokenProvider.validateToken(token)) { throw new BadRequestException(ErrorResponseStatus.INVALID_TOKEN); }

        // 2. 사용자 이메일 추출
        String userEmail = jwtTokenProvider.getUserEmail(token);

        try {
            tripService.deleteEvent(userEmail, id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/event/{id}")
    @Operation(summary = "일정 조회 API", description = "tripID 입력 시 여행 정보 반환 - 조건이 없으면 사용자의 모든 일정 반환")
    public ResponseEntity<List<TripResponse.TripInfoDTO>> getEvent(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id) {

        // 1. 토큰 확인
        if(!jwtTokenProvider.validateToken(token)) { throw new BadRequestException(ErrorResponseStatus.INVALID_TOKEN); }

        // 2. 토큰 -> 사용자 아이디
        Long userId = jwtTokenProvider.getUserId(token);
        List<TripResponse.TripInfoDTO> events = tripService.getEvents(userId, id);
        return ResponseEntity.ok(events);
    }
}

