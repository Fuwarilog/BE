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
        long id;
        String title;
        LocalDate startDate;
        LocalDate endDate;
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
