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
        private long bookmarkId;
        private long postId;
        private long userId;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static PostBookmarkStateDTO from(PostBookmark postBookmark) {
            return new  PostBookmarkStateDTO(
                    postBookmark.getId(),
                    postBookmark.getPost().getId(),
                    postBookmark.getUser().getId(),
                    postBookmark.getCreatedAt(),
                    postBookmark.getUpdatedAt());
        }
    }
}
