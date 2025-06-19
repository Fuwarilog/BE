package com.skuniv.fuwarilog.controller;

import com.skuniv.fuwarilog.config.exception.BadRequestException;
import com.skuniv.fuwarilog.config.exception.ErrorResponseStatus;
import com.skuniv.fuwarilog.domain.User;
import com.skuniv.fuwarilog.dto.Post.PostResponse;
import com.skuniv.fuwarilog.dto.PostBookmark.PostBookmarkResponse;
import com.skuniv.fuwarilog.dto.PostLike.PostLikeResponse;
import com.skuniv.fuwarilog.repository.UserRepository;
import com.skuniv.fuwarilog.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
@Tag(name = "Post API", description = "게시글 관련 조회, 북마크, 좋아요 관련 기능")
public class PostController {

    private final PostService postService;
    private final UserRepository userRepository;

    @GetMapping("")
    @Operation(summary = "포스트 조회 API", description = "공개 처리된 게시글 최신순으로 반환")
    public ResponseEntity<List<PostResponse.PostListDTO>> getPosts (
            Authentication authentication) {

        String email = (String) authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.USER_NOT_FOUND));
        // 3. 포스트 조회
        List<PostResponse.PostListDTO> results = postService.getPosts(user.getId());
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{postId}")
    @Operation(summary = "특정 포스트 상세 조회 API", description = "postId 입력시 해당 포스트의 상세 정보 조회")
    public ResponseEntity<PostResponse.PostInfoDTO> getPostContent (
            Authentication authentication,
            @PathVariable long postId,
            HttpServletRequest request) {

        String email = (String) authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.USER_NOT_FOUND));

        String ipAdress = extractClientIp(request);

        PostResponse.PostInfoDTO results = postService.getPostContent(user.getId(), postId, ipAdress);
        return ResponseEntity.ok(results);
    }

    private String extractClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isEmpty()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    @PostMapping("/bookmarks/{postId}")
    @Operation(summary = "게시글 북마크 등록/취소 기능", description = "게시글 아이디 입력하면 북마크 등록 및 취소 상태 반환")
    public ResponseEntity<PostBookmarkResponse.PostBookmarkStateDTO> editPostBookmark (
            Authentication authentication,
            @PathVariable(required = true) long postId) {

        String email = (String) authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.USER_NOT_FOUND));

        PostBookmarkResponse.PostBookmarkStateDTO result = postService.editPostBookmark(user.getId(), postId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/likes/{postId}")
    @Operation(summary = "게시글 좋아요 등록/취소 기능", description = "게시글 아이디 입력 시 좋아요 등록 및 취소 상태 반환")
    public ResponseEntity<PostLikeResponse.PostLikesStateDTO> editPostLikes (
            Authentication authentication,
            @PathVariable(required = true) long postId) {

        String email = (String) authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.USER_NOT_FOUND));

        PostLikeResponse.PostLikesStateDTO result = postService.editPostLikes(user.getId(), postId);
        return ResponseEntity.ok(result);
    }
}
