package com.skuniv.fuwarilog.controller;

import com.skuniv.fuwarilog.config.exception.BadRequestException;
import com.skuniv.fuwarilog.config.exception.ErrorResponseStatus;
import com.skuniv.fuwarilog.security.jwt.JwtTokenProvider;
import com.skuniv.fuwarilog.service.DiaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Slf4j
@Tag(name = "Diary API", description = "다이어리 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/diaries")
public class DiaryController {

    private final JwtTokenProvider jwtTokenProvider;
    private final DiaryService diaryService;

    @GetMapping("/diary/init")
    @Operation(summary = "다이어리 포스트 내용 초기화 API", description = "해당 날짜의 장소/태그 데이터 저장")
    public ResponseEntity<?> getDiaryInitData(
            @RequestHeader("Authorization") String token,
            @RequestParam String date) {

        // 1. 토큰 확인
        if(!jwtTokenProvider.validateToken(token)) { throw new BadRequestException(ErrorResponseStatus.INVALID_TOKEN); }

        // 2. 사용자 아이디 추출
        Long userId = jwtTokenProvider.getUserId(token);
        String content = diaryService.buildDiaryContent(userId, LocalDate.parse(date));
        return ResponseEntity.ok(content); // mongoDB
    }
}

