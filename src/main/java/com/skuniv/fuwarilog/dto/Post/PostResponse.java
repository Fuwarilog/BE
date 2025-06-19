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
        @Schema(description = "게시글 고유번호" , example = "1")
        private long id;
        @Schema(description = "사용자 고유번호" , example = "1")
        private long userId;
        @Schema(description = "사용자 이름" , example = "홍길동")
        private String userName;
        @Schema(description = "다이어리 제목" , example = "일본여행")
        private String title;
        @Schema(description = "게시글 날짜" , example = "2025-06-01")
        private LocalDate date;
        @Schema(description = "좋아요 개수" , example = "1")
        private int likesCount;
        @Schema(description = "조회수" , example = "1")
        private int watchCount;
        @Schema(description = "게시글 생성일" , example = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdDate;
        @Schema(description = "게시글 수정일" , example = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime updatedDate;
    }

    @Getter
    @Setter
    @Builder
    @Schema(title = "RES 02 : 특정 게시글 정보 반환 DTO")
    public static class PostInfoDTO {
        @Schema(description = "게시글 고유번호" , example = "1")
        private Long id;
        @Schema(description = "사용자 고유번호" , example = "1")
        private Long userId;
        @Schema(description = "사용자 이름" , example = "홍길동")
        private String userName;
        @Schema(description = "다이어리 제목" , example = "일본여행")
        private String title;
        @Schema(description = "다이어리 고유번호" , example = "1")
        private Long diaryListId;
        @Schema(description = "다이어리 내용" , example = "1일차 일본여행에서 ....")
        private String content;
        @Schema(description = "게시글 날짜" , example = "2025-06-01")
        private LocalDate date;
        @Schema(description = "좋아요 개수" , example = "1")
        private int likesCount;
        @Schema(description = "사용자의 좋아요 상태" , example = "true")
        private Boolean likeState;
        @Schema(description = "조회수" , example = "1")
        private int watchCount;
        @Schema(description = "북마크 상태" , example = "true")
        private Boolean bookmarkState;
        @Schema(description = "게시글 생성일" , example = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdDate;
        @Schema(description = "게시글 수정일" , example = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime updatedDate;
    }
}
