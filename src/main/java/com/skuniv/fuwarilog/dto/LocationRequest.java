package com.skuniv.fuwarilog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

public class LocationRequest {

    @Getter
    @Setter
    @Data
    @Schema(title = "REQ 1: 경로 탐색 DTO")
    public static class RouteReqDTO {
        String origin;
        String destination;
    }

    @Data
    @Builder
    @Getter
    @Setter
    @Schema(title = "REQ 2: 지도 북마크 DTO")
    public static class MapBookmarkReqDTO {
        String name;
        double latitude;
        double longitude;
        boolean bookmarked;
    }

    @Getter
    @Setter
    @Builder
    @Data
    @Schema(title = "REQ 3: 현재 위치 DTO")
    public static class LocationReqDTO {
        double latitude;
        double longitude;
    }
}
