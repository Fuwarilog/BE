package com.skuniv.fuwarilog.dto.Auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class AuthResponse {
    @Getter
    @Setter
    @Builder
    @Schema(title = "RES 01: 회원가입 반환 DTO")
    public static class resRegisterDTO {
        @Schema(description = "사용자 고유번호", example = "1")
        long userId;
        @Schema(description = "사용자 이름", example = "홍길동")
        String name;
        @Schema(description = "사용자 이메일", example = "test123@naver.com")
        String email;
        @Schema(description = "사용자 비밀번호", example = "test123@")
        String password;
        @Schema(description = "sns로그인", example = "google")
        String provider;
    }
}
