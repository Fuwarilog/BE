package com.skuniv.fuwarilog.dto;

import com.skuniv.fuwarilog.domain.DiaryList;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

public class DiaryListResponse {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(title = "RES 1. 다이어리 폴더 내 리스트 조회 DTO")
    public static class DiaryListResDTO {
        private long id;
        private long diaryId;
        private LocalDate date;
        private Boolean isPublic;
        private Integer likes;

        public static DiaryListResDTO from(DiaryList diaryList) {
            return new DiaryListResDTO(
                    diaryList.getId(),
                    diaryList.getDiary().getId(),
                    diaryList.getDate(),
                    diaryList.isPublic(),
                    diaryList.getLikeCount());
        }
    }
}
