package com.skuniv.fuwarilog.dto.Post;

import com.skuniv.fuwarilog.domain.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PostResponse {
    @Getter
    @Setter
    @Builder
    @Schema(title = "RES 01: 게시글 반환 DTO")
    public static class PostListDTO {
        private long id;
        private long userId;
        private String userName;
        private String title;
        private LocalDate date;
        private int likesCount;
        private int watchCount;
        private LocalDateTime createdDate;
        private LocalDateTime updatedDate;
    }
}
