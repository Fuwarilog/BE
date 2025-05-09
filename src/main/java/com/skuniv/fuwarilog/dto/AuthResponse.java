package com.skuniv.fuwarilog.dto;

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
        long userId;
        String name;
        String email;
        String password;
    }
}
