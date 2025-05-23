package com.skuniv.fuwarilog.dto;

import com.skuniv.fuwarilog.domain.Diary;
import com.skuniv.fuwarilog.domain.Trip;
import com.skuniv.fuwarilog.domain.User;
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
        private long tripId;
        private String title;
        private String description;
        private String country;
        private String eventId;
        private LocalDate startDate;
        private LocalDate endDate;
        private List<DiaryResponse.DiaryResDTO> diaries;

    }
}
