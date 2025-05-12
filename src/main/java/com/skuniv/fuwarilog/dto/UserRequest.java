package com.skuniv.fuwarilog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

public class UserRequest {

    @Getter
    @Builder
    @Schema(title = "REQ 01 : 사용자 정보 수정 DTO")
    public static class UserInfoDTO {
        @Schema(name="이름", example = "홍길동")
        private String name;

        @Schema(name="이메일", example = "test123@naver.com")
        private String email;

        @Schema(name="비밀번호", example = "test123")
        private String password;

        @Schema(name="프로필 사진", example = "url")
        private String pictureUrl;
    }
}
