package com.skuniv.fuwarilog.controller;

import com.skuniv.fuwarilog.config.exception.BadRequestException;
import com.skuniv.fuwarilog.config.exception.ErrorResponseStatus;
import com.skuniv.fuwarilog.dto.Post.PostResponse;
import com.skuniv.fuwarilog.dto.PostBookmark.PostBookmarkResponse;
import com.skuniv.fuwarilog.security.jwt.JwtTokenProvider;
import com.skuniv.fuwarilog.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
@Tag(name = "Post API", description = "게시글 관련 조회, 북마크, 좋아요 관련 기능")
public class PostController {

    private final JwtTokenProvider jwtTokenProvider;
    private final PostService postService;

    @GetMapping("/")
    @Operation(summary = "포스트 조회 API", description = "공개 처리된 게시글 최신순으로 반환")
    public ResponseEntity<List<PostResponse.PostListDTO>> getPosts (
            @RequestHeader("Authorization") String token) {

        // 1. 토큰 검증
        if(!jwtTokenProvider.validateToken(token)) { throw new BadRequestException(ErrorResponseStatus.INVALID_TOKEN);}

        // 2. 사용자 고유 번호 추출
        Long userId = jwtTokenProvider.getUserId(token);

        // 3. 포스트 조회
        List<PostResponse.PostListDTO> results = postService.getPosts(userId);
        return ResponseEntity.ok(results);
    }

    @PostMapping("/{postId}")
    @Operation(summary = "게시글 북마크 등록/취소 기능", description = "게시글 아이디 입력하면 북마크 등록 및 취소 상태")
    public ResponseEntity<PostBookmarkResponse.PostBookmarkStateDTO> editPostBookmark (
            @RequestHeader("Authorization") String token,
            @PathVariable(required = true) long postId,
            @RequestParam(required = true) boolean state) {
        // 1. 토큰 검증
        if(!jwtTokenProvider.validateToken(token)) { throw new BadRequestException(ErrorResponseStatus.INVALID_TOKEN);}

        // 2. 사용자 고유 번호 추출
        Long userId = jwtTokenProvider.getUserId(token);

        PostBookmarkResponse.PostBookmarkStateDTO result = postService.editPostBookmark(userId, postId, state);
        return ResponseEntity.ok(result);
    }
}
