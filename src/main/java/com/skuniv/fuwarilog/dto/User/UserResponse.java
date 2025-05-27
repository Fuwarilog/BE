package com.skuniv.fuwarilog.dto.User;

import com.skuniv.fuwarilog.domain.PostBookmark;
import com.skuniv.fuwarilog.domain.PostLike;
import com.skuniv.fuwarilog.domain.User;
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
        List<PostBookmark> bookmarks;
        List<PostLike> likes;

        public static UserInfoDTO from(User user) {
            return new UserInfoDTO(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getPassword(),
                    user.getPictureUrl(),
                    user.getCreatedAt(),
                    user.getUpdatedAt(),
                    user.getBookmarks(),
                    user.getLikes());
        }

    }

}
