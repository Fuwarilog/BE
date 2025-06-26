package com.skuniv.fuwarilog.dto.PostBookmark;

import com.skuniv.fuwarilog.domain.PostBookmark;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public class PostBookmarkResponse {

    @Getter
    @Setter
    @Builder
    @Schema(title = "RES 01: 게시글 북마크 상태 반환 DTO")
    public static class PostBookmarkStateDTO {
        @Schema(name="게시글 고유 번호", example = "1")
        private long postId;
        @Schema(name="사용자 고유 번호", example = "1")
        private long userId;
        @Schema(name="n", example = "False")
        private boolean bookmarked;

        public static PostBookmarkStateDTO of(long postId, long userId, boolean bookmarked) {
            return PostBookmarkStateDTO.builder()
                    .postId(postId)
                    .userId(userId)
                    .bookmarked(bookmarked)
                    .build();
        }
    }
}
