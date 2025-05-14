package com.skuniv.fuwarilog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

public class MapRequest {

    @Data
    @Builder
    @Getter
    @Setter
    @Schema(title = "REQ 1: 지도 검색 DTO")
    public static class MapSearchReqDTO {

    }

    @Data
    @Builder
    @Getter
    @Setter
    @Schema(title = "REQ 2: 지도 북마크 DTO")
    public static class MapBookmarkReqDTO {

    }

    @Getter
    @Setter
    @Builder
    @Data
    @Schema(title = "REQ 3: 현재 위치 DTO")
    public static class LocationReqDTO {
        String placeName;
        String address;
        double latitude;
        double longitude;
    }

    @Getter
    @Setter
    @Builder
    @Data
    @Schema(title = "REQ 4: 경로 탐색 DTO")
    public class RouteReqDTO {

    }
}
