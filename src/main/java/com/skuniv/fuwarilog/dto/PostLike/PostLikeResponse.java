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
        private long postId;
        private long userId;
        private boolean liked;

        public static PostLikesStateDTO of(long postId, long userId, boolean liked) {
            return PostLikesStateDTO.builder()
                    .postId(postId)
                    .userId(userId)
                    .liked(liked)
                    .build();
        }
    }
}
