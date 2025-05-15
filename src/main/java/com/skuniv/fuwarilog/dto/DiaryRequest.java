package com.skuniv.fuwarilog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

public class DiaryRequest {

    @Data
    @Getter
    @Setter
    @Builder
    @Schema(title = "REQ 1: 다이어리 정보 데이터 DTO")
    public static class DiaryInfoDTO {
        @Schema(name="title", description = "여행일정 제목" , example = "대만 여행")
        String title;

        @Schema(name="startDate", example = "yyyy-MM-dd")
        LocalDate startDate;

        @Schema(name="endDate", example = "yyyy-MM-dd")
        LocalDate endDate;
    }
}
