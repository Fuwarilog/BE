package com.skuniv.fuwarilog.controller;

import com.skuniv.fuwarilog.config.exception.BadRequestException;
import com.skuniv.fuwarilog.config.exception.ErrorResponseStatus;
import com.skuniv.fuwarilog.domain.DiaryContent;
import com.skuniv.fuwarilog.domain.User;
import com.skuniv.fuwarilog.dto.DiaryContent.DiaryContentRequest;
import com.skuniv.fuwarilog.dto.DiaryList.DiaryListResponse;
import com.skuniv.fuwarilog.dto.Trip.TripResponse;
import com.skuniv.fuwarilog.repository.UserRepository;
import com.skuniv.fuwarilog.service.DiaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/diaries")
@Tag(name = "Diary API", description = "다이어리 관련 조회, 작성(태그 관리), 삭제")
public class DiaryController {
    private final DiaryService diaryService;
    private final UserRepository userRepository;

    @GetMapping("")
    @Operation(summary = "다이어리 폴더 조회 API", description = "사용자 id 입력 시 다이어리 폴더 조회")
    public ResponseEntity<List<TripResponse.TripInfoDTO>> getAllDiaries(
            Authentication authentication) {

        String email = (String) authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.USER_NOT_FOUND));

        return ResponseEntity.ok(diaryService.getAllDiaries(user.getId()));
    }


    @GetMapping("/{diaryId}")
    @Operation(summary = "다이어리 폴더 내 리스트 조회", description = "사용자 id, 다이어리 폴더 id 입력 시 리스트 조회")
    public ResponseEntity<List<DiaryListResponse.DiaryListResDTO>> getAllDiaryList(
            Authentication authentication,
            @PathVariable Long diaryId,
            @RequestParam(required = false) Boolean isPublic) {

        String email = (String) authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.USER_NOT_FOUND));

        return ResponseEntity.ok(diaryService.getAllDiaryList(user.getId(), diaryId, isPublic));
    }

    @PostMapping(value = "/content/{diaryListId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "다이어리 새로 생성 API", description = "diaryListId, 내용 입력 시 새로운 다이어리로 작성 완료")
    public ResponseEntity<?> createDiaryContent(
            Authentication authentication,
            @PathVariable Long diaryListId,
            @RequestPart(required = false) DiaryContentRequest.ContentDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        String email = (String) authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.USER_NOT_FOUND));

        // 3. 내용 작성
        DiaryContent result = diaryService.createDiaryContent(dto, diaryListId, user.getId(), image);
        return ResponseEntity.ok(result);
    }

    @PutMapping(value = "/content/{diaryListId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "기존 다이어리 내용 작성 API", description = "diaryListId를 입력, 내용, tag 입력 시 기존 다이어리의 내용 수정 완료")
    public ResponseEntity<?> editDiaryContent(
            Authentication authentication,
            @PathVariable Long diaryListId,
            @RequestParam(required = false) String tag,
            @RequestPart(required = false) DiaryContentRequest.ContentDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        String email = (String) authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.USER_NOT_FOUND));

        // 3. 내용 작성
        DiaryContent result = diaryService.editDiaryContent(dto, diaryListId, user.getId(), image, tag);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/content/{diaryListId}")
    @Operation(summary = "다이어리 내용 조회 API", description = "다이어리 내용 조회")
    public ResponseEntity<?> getContent(
            Authentication authentication,
            @PathVariable Long diaryListId) {

        String email = (String) authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.USER_NOT_FOUND));

        DiaryContent content = diaryService.getDiaryContent(user.getId(), diaryListId);
        return ResponseEntity.ok(content);
    }

    @PostMapping("/content/public/{diaryListId}")
    @Operation(summary = "다이어리 공개여부 설정 API", description = " diaryListId, isPublic 입력 시 공개여부 설정 변경됨")
    public ResponseEntity<DiaryListResponse.isPublicDiaryDTO> isPublicDiary(
            Authentication authentication,
            @PathVariable Long diaryListId,
            @RequestParam(required = true) Boolean isPublic) {

        String email = (String) authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.USER_NOT_FOUND));

        // 3. 공개여부 설정
        DiaryListResponse.isPublicDiaryDTO result = diaryService.isPublicDiary(diaryListId, user.getId(), isPublic);
        return ResponseEntity.ok(result);
    }

}
