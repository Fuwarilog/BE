package com.skuniv.fuwarilog.dto.DiaryList;

import com.skuniv.fuwarilog.domain.DiaryList;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;

public class DiaryListResponse {

    @Getter
    @Setter
    @Builder
    @Schema(title = "RES 1. 다이어리 폴더 내 리스트 조회 DTO")
    public static class DiaryListResDTO {
        private long id;
        private long diaryId;
        private LocalDate date;
        private Boolean isPublic;

        public static DiaryListResDTO from(DiaryList diaryList) {
            return new DiaryListResDTO(
                    diaryList.getId(),
                    diaryList.getDiary().getId(),
                    diaryList.getDate(),
                    diaryList.getIsPublic());
        }
    }

    @Getter
    @Setter
    @Builder
    @Schema(title = "RES 2. 다이어리 공개여부 반환 DTO")
    public static class isPublicDiaryDTO {
        private long id;
        private Boolean isPublic;

        public static isPublicDiaryDTO from(DiaryList diaryList) {
            return new isPublicDiaryDTO(
                    diaryList.getId(),
                    diaryList.getIsPublic());
        }
    }
}
