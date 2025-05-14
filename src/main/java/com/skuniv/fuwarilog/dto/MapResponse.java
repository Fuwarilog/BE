package com.skuniv.fuwarilog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

public interface MapResponse {

    @Data
    @Schema(title = "RES 1: 지도 검색 반환 DTO")
    public static class MapSearchResDTO {

    }

    @Data
    @Schema(title = "RES 2: 지도 북마크 반환 DTO")
    public static class MapBookmarkResDTO {

    }

    public record CurrentLocationDTO (String placeName, double latitude, double longitude) {};
    public record PlaceDTO (String name, double latitude, double longitude) {};
    public record RouteDTO (String duration,  double lat, double lon) {};

}
