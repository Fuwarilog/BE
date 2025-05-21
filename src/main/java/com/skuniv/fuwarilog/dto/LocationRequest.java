package com.skuniv.fuwarilog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

public class LocationRequest {

    @Builder
    @Getter
    @Setter
    @Schema(title = "REQ 1: 지도 북마크 DTO")
    public static class LocationBookmarkReqDTO {
        @Schema(description = "장소 이름", example = "서경대학교")
        String name;
        @Schema(description = "위도", example = "37.5808")
        double latitude;
        @Schema(description = "경도", example = "127.0238")
        double longitude;
    }

    @Getter
    @Setter
    @Builder
    @Schema(title = "REQ 2: 현재 위치 DTO")
    public static class LocationReqDTO {
        @Schema(description = "위도", example = "37.5808")
        double latitude;
        @Schema(description = "경도", example = "127.0238")
        double longitude;
    }
}
