package com.skuniv.fuwarilog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

public class PostResponse {
    @Getter
    @Setter
    @Builder
    @Schema(title = "RES 01: 게시글 반환 DTO")
    public static class PostListDTO {
        private long id;
        private LocalDate date;
        private int likesCount;
        private int watchCount;
        private LocalDate createdDate;
        private LocalDate updatedDate;
    }
}
