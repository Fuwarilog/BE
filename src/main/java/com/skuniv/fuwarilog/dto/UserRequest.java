package com.skuniv.fuwarilog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class UserRequest {

    @Setter
    @Getter
    @Builder
    @Schema(title = "REQ 01 : 사용자 정보 수정 DTO")
    public static class UserInfoDTO {
        @Schema(name="name", example = "홍길동")
        private String name;

        @Schema(name="email", example = "test123@naver.com")
        private String email;

        @Schema(name="password", example = "test123")
        private String password;

        @Schema(name="pictureUrl", example = "url")
        private String pictureUrl;
    }
}
