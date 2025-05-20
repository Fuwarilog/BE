package com.skuniv.fuwarilog.controller;

import com.skuniv.fuwarilog.config.exception.BadRequestException;
import com.skuniv.fuwarilog.config.exception.ErrorResponseStatus;
import com.skuniv.fuwarilog.domain.Diary;
import com.skuniv.fuwarilog.domain.DiaryContent;
import com.skuniv.fuwarilog.dto.DiaryContentRequest;
import com.skuniv.fuwarilog.dto.DiaryResponse;
import com.skuniv.fuwarilog.security.jwt.JwtTokenProvider;
import com.skuniv.fuwarilog.service.DiaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/diaries")
@Tag(name = "Diary API", description = "다이어리 관련 조회, 작성, 삭제 + 지도맵의 태그 관리")
public class DiaryController {
    private final DiaryService diaryService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/")
    @Operation(summary = "다이어리 폴더 조회 API", description = "사용자 id 입력 시 다이어리 폴더 조회")
    public ResponseEntity<List<DiaryResponse.DiaryResDTO>> getAllDiaries(
            @RequestHeader("Authorization") String token) {
        // 1. 토큰 검증
        if(!jwtTokenProvider.validateToken(token)) { throw new BadRequestException(ErrorResponseStatus.INVALID_TOKEN);}

        // 2. 사용자 고유 번호 추출
        Long userId = jwtTokenProvider.getUserId(token);
        return ResponseEntity.ok(diaryService.getAllDiaries(userId));
    }

//
//    @GetMapping("/list")
//    @Operation(summary = "다이어리 폴더 내 리스트 조회", description = "사용자 id, 다이어리 폴더 id 입력 시 리스트 조회")


    @PostMapping("/list/content")
    @Operation(summary = "다이어리 내용 작성 API", description = "diaryListId, 내용 입력 시 작성 완료")
    public ResponseEntity<?> saveContent(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = true) Long diaryListId,
            @RequestBody DiaryContentRequest.ContentDTO dto) {

        if(!jwtTokenProvider.validateToken(token)) { throw new BadRequestException(ErrorResponseStatus.INVALID_TOKEN);}

        Long userId = jwtTokenProvider.getUserId(token);
        DiaryContent saved = diaryService.saveOrUpdateDiaryContent(dto, diaryListId, userId);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/list/content")
    @Operation(summary = "다이어리 내용 조회 API", description = "다이어리 내용 조회")
    public ResponseEntity<?> getContent(
            @RequestHeader("Authorization") String token,
            @RequestParam Long diaryListId) {

        Long userId = jwtTokenProvider.getUserId(token);
        DiaryContent content = diaryService.getDiaryContent(userId, diaryListId);
        return ResponseEntity.ok(content);
    }

    @DeleteMapping("/list/content/delete-tag")
    @Operation(summary = "다이어리 내용 태그 삭제 API", description = "특정 태그String 입력 시 다이어리 내용의 해당 태그 삭제")
    public ResponseEntity<?> deleteTagFromContent(
            @RequestParam String tag,
            @RequestParam Long diaryListId,
            @RequestParam Long userId) {

        diaryService.removeTagFromContent(userId, diaryListId, tag);
        return ResponseEntity.ok("태그 삭제 완료");
    }

}
