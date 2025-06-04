package com.skuniv.fuwarilog.dto.Location;

import com.skuniv.fuwarilog.domain.Location;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

public class LocationResponse {
    public record CurrentLocationDTO (String placeName, double latitude, double longitude) {};
    public record PlaceDTO (String name, String address, double latitude, double longitude, String placeId) {};

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(title = "RES 1: 위치 북마크 정보 반환 DTO")
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

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @Schema(title = "RES 2: 장소 상세 정보 조회 DTO")
    public static class LocationDetailDTO {
        private String name;
        private String phone;
        private String address;
        private Double rating;
        private double lat;
        private double lng;
        private List<String> openingHours;

        public LocationDetailDTO(String name, String phone,  String address, Double rating, double lat, double lng, List<String> openingHours) {
            this.name = name;
            this.phone = phone;
            this.address = address;
            this.rating = rating;
            this.lat = lat;
            this.lng = lng;
            this.openingHours = openingHours;
        }
    }
}
