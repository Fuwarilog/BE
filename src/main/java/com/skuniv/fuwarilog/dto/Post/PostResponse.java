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
        private LocalDate date;
        private int likesCount;
        private int watchCount;
        private LocalDateTime createdDate;
        private LocalDateTime updatedDate;

        public static PostResponse.PostListDTO from(Post post) {
            return new PostListDTO(
                    post.getId(),
                    post.getDiaryList().getDate(),
                    post.getLikesCount(),
                    post.getWatchCount(),
                    post.getCreatedAt(),
                    post.getUpdatedAt());
        }
    }
}
