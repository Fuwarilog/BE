package com.skuniv.fuwarilog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

public class LocationRequest {

    @Data
    @Builder
    @Getter
    @Setter
    @Schema(title = "REQ 1: 지도 북마크 DTO")
    public static class LocationBookmarkReqDTO {
        String name;
        double latitude;
        double longitude;
        boolean bookmarked;
    }

    @Getter
    @Setter
    @Builder
    @Data
    @Schema(title = "REQ 2: 현재 위치 DTO")
    public static class LocationReqDTO {
        double latitude;
        double longitude;
    }
}
