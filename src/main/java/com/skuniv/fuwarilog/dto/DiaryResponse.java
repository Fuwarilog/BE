package com.skuniv.fuwarilog.dto;

import com.skuniv.fuwarilog.domain.Diary;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;

public class DiaryResponse {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(title = "RES 1. 다이어리 폴더 조회")
    public static class DiaryResDTO {
        @Schema(description = "다이어리 고유번호" , example = "1")
        long id;
        @Schema(description = "여행 제목" , example = "대만 여행")
        String title;
        @Schema(description = "여행 시작일" , example = "yyyy-MM-dd HH:mm:ss")
        LocalDate startDate;
        @Schema(description = "여행 종료일" , example = "yyyy-MM-dd HH:mm:ss")
        LocalDate endDate;
        @Schema(description = "여행일정 고유번호" , example = "1")
        long tripId;

        public static DiaryResDTO from(Diary diary) {
            return new DiaryResDTO(
                    diary.getId(),
                    diary.getTitle(),
                    diary.getStartDate(),
                    diary.getEndDate(),
                    diary.getTrip().getId());
        }
    }
}
