package com.skuniv.fuwarilog.dto.Auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class AuthRequest {

    @Getter
    @Setter
    @Builder
    @Schema(title = "REQ 01: 로그인 요청 DTO")
    public static class postLoginDTO {
        @Schema(example = "test123@naver.com")
        String email;
        @Schema(example = "test123!")
        String password;
    }

    @Getter
    @Setter
    @Builder
    @Schema(title = "REQ 02: 회원가입 요청 DTO")
    public static class postRegisterDTO {
        @Schema(example = "홍길동")
        String name;
        @Schema(example = "test123@naver.com")
        String email;
        @Schema(example = "test123!")
        String password;
    }

}
