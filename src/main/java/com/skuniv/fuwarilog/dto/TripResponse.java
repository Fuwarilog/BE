package com.skuniv.fuwarilog.dto;

import com.skuniv.fuwarilog.domain.Trip;
import com.skuniv.fuwarilog.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TripResponse {

    @Getter
    @Setter
    @Builder
    @Schema(title = "RES 01: 여행일정 반환 DTO")
    public static class TripInfoDTO {
        long tripId;
        String title;
        String description;
        String country;
        String eventId;
        LocalDate startDate;
        LocalDate endDate;

        public static TripResponse.TripInfoDTO from(Trip trip) {
            return new TripResponse.TripInfoDTO(
                    trip.getId(),
                    trip.getTitle(),
                    trip.getDescription(),
                    trip.getCountry(),
                    trip.getGoogleEventId(),
                    trip.getStartDate(),
                    trip.getEndDate());
        }
    }
}
