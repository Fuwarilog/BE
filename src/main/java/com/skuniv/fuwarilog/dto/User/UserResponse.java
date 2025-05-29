package com.skuniv.fuwarilog.dto.User;

import com.skuniv.fuwarilog.domain.PostBookmark;
import com.skuniv.fuwarilog.domain.PostLike;
import com.skuniv.fuwarilog.domain.User;
import com.skuniv.fuwarilog.dto.PostLike.PostLikeResponse;
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
        String pictureUrl;
        LocalDateTime createdAt;
        LocalDateTime updatedAt;

        public static UserInfoDTO from(User user) {
            return new UserInfoDTO(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getPictureUrl(),
                    user.getCreatedAt(),
                    user.getUpdatedAt());
        }

    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(title = "RES 02: 사용자 게시글 좋아요 리스트 전달 DTO")
    public static class UserPostLikeDTO {
        Long userId;
        List<PostLikeResponse.PostLikesStateDTO>  postLikes;
    }
}
