package com.skuniv.fuwarilog.dto.Trip;

import com.skuniv.fuwarilog.domain.Trip;
import com.skuniv.fuwarilog.dto.Diary.DiaryResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class TripResponse {

    @Getter
    @Setter
    @Builder
    @Schema(title = "RES 01: 여행일정 반환 DTO")
    public static class TripInfoDTO {
        @Schema(description = "사용자 고유번호", example = "1")
        private long userId;

        @Schema(description = "일정 고유번호", example = "1")
        private long tripId;

        @Schema(description = "제목", example = "제목")
        private String title;

        @Schema(description = "설명", example = "string")
        private String description;

        @Schema(description = "국가", example = "미국")
        private String country;

        @Schema(description = "구글캘린더Id", example = "string")
        private String eventId;

        @Schema(description = "시작일", example = "2025-06-20")
        private LocalDate startDate;

        @Schema(description = "종료일", example = "2025-06-21")
        private LocalDate endDate;

        @Schema(description = "다이어리 목록", example = "...")
        private List<DiaryResponse.DiaryResDTO> diaries;
    }

    @Getter
    @Setter
    @Builder
    @Schema(title = "RES 02: 여행일정 목록반환 DTO")
    public static class TripListDTO {
        @Schema(description = "고유번호", example = "1")
        private long tripId;

        @Schema(description = "제목", example = "대만여행")
        private String title;

        @Schema(description = "google 이벤트 아이디", example = "...")
        private String eventId;

        @Schema(description = "시작일", example = "yyyy-MM-dd HH:mm:ss")
        private LocalDate startDate;

        @Schema(description = "종료일", example = "yyyy-MM-dd HH:mm:ss")
        private LocalDate endDate;

        @Schema(description = "생성일", example = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt;

        @Schema(description = "수정일", example = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime updatedAt;

        public static TripResponse.TripListDTO from(Trip trip) {
            return new TripListDTO(
                    trip.getId(),
                    trip.getTitle(),
                    trip.getDescription(),
                    trip.getStartDate(),
                    trip.getEndDate(),
                    trip.getCreatedAt(),
                    trip.getUpdatedAt());
        }
    }
}