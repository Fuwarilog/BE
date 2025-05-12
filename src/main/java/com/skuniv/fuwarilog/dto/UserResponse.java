package com.skuniv.fuwarilog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class UserResponse {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(title = "RES 01: 사용자 정보 전달 DTO")
    public static class UserInfoDTO {
        Long id;
        String name;
        String email;
        String password;
        String pictureUrl;
        LocalDateTime createdAt;
        LocalDateTime updatedAt;
        List<BookmarkDTO> bookmarks;
        List<LikeDTO> likes;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(title = "RES 02: 사용자 북마크 리스트 전달 DTO")
    public static class BookmarkDTO {
        Long id;
        Long userId;
        Long contentId;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(title = "RES 03: 사용자 좋아요 리스트 전달 DTO")
    public static class LikeDTO {
        Long id;
        Long userId;
        Long contentId;
    }
}
