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
    }
}
