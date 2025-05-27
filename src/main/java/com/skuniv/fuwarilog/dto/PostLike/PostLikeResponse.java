package com.skuniv.fuwarilog.dto.PostLike;

import com.skuniv.fuwarilog.domain.PostLike;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public class PostLikeResponse {

    @Getter
    @Setter
    @Builder
    @Schema(title = "RES 01: 게시글 좋아요 정보 반환 DTO")
    public static class PostLikesStateDTO {
        private long likeId;
        private long postId;
        private long userId;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static PostLikesStateDTO from(PostLike postLike) {
            return new  PostLikesStateDTO(
                    postLike.getId(),
                    postLike.getPost().getId(),
                    postLike.getUser().getId(),
                    postLike.getCreatedAt(),
                    postLike.getUpdatedAt());
        }
    }
}
