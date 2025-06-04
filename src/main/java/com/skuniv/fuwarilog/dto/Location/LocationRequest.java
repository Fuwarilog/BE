package com.skuniv.fuwarilog.dto.Location;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class LocationRequest {

    @Builder
    @Getter
    @Setter
    @Schema(title = "REQ 1: 지도 북마크 DTO")
    public static class LocationBookmarkReqDTO {
        @Schema(description = "장소 고유번호", example = "구글맵의 placeId")
        private String placeId;

        @Schema(description = "장소 이름", example = "서경대학교")
        private String name;

        @Schema(description = "장소 URL")
        private String url;

        @Schema(description = "장소 주소", example = "서울특별시 강북구 솔샘로")
        private String address;

        @Schema(description = "위도", example = "37.5808")
        private double latitude;

        @Schema(description = "경도", example = "127.0238")
        private double longitude;
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
