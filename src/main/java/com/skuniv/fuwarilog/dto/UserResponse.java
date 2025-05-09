package com.skuniv.fuwarilog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

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
        String pictureUrl;
        List<BookmarkDTO> bookmarks;
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
}
