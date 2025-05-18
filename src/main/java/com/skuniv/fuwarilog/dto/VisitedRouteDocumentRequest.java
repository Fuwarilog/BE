package com.skuniv.fuwarilog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

public class VisitedRouteDocumentRequest {

    @Getter
    @Setter
    @Data
    @Builder
    @Schema(title = "REQ 1: 경로 탐색 DTO")
    public static class RouteRequestDTO {
        private String origin;
        private String destination;
        private LocalDate tripDate;
    }
}
