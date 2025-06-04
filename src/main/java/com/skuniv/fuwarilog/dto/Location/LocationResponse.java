package com.skuniv.fuwarilog.dto.Location;

import com.skuniv.fuwarilog.domain.Location;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class LocationResponse {
    public record CurrentLocationDTO (String placeName, double latitude, double longitude) {};
    public record PlaceDTO (String name, String address, double latitude, double longitude, String placeId) {};

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(title = "RES 1. 위치 북마크 정보 반환 DTO")
    public static class LocationInfoDTO {
        @Schema(description = "장소 고유 번호", example = "1")
        private long id;
        @Schema(description = "장소 이름", example = "서경대학교")
        private String placeName;
        @Schema(description = "구글맵 위치 고유번호")
        private String placeId;
        @Schema(description = "위도", example = "37.5808")
        private double latitude;
        @Schema(description = "경도", example = "127.0238")
        private double longitude;
        @Schema(description = "주소", example = "서울시 강북구..")
        private String address;
        @Schema(description = "장소 URL")
        private String url;

        public static LocationInfoDTO from(Location location) {
            return new LocationInfoDTO(
                    location.getId(),
                    location.getPlaceName(),
                    location.getPlaceId(),
                    location.getLongitude(),
                    location.getLongitude(),
                    location.getAddress(),
                    location.getPlaceUrl());
        }
    }
}
