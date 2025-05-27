package com.skuniv.fuwarilog.dto.Trip;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TripRequest {

    @Data
    @Getter
    @Setter
    @Builder
    @Schema(title = "REQ 1: 여행 일정 데이터 DTO")
    public static class TripInfoDTO {
        @Schema(name="title", description = "구글캘린더 제목" , example = "대만 여행")
        String title;

        @Schema(name="description", description = "구글캘린더 설명", example = "대만 여행 3박 4일")
        String description;

        @Schema(name="country", example = "대만")
        String country;

        @Schema(name="startDate", example = "yyyy-MM-dd")
        LocalDate startDate;

        @Schema(name="endDate", example = "yyyy-MM-dd")
        LocalDate endDate;

    }
}
