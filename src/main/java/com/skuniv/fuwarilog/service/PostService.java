package com.skuniv.fuwarilog.service;

import com.skuniv.fuwarilog.config.exception.BadRequestException;
import com.skuniv.fuwarilog.config.exception.ErrorResponseStatus;
import com.skuniv.fuwarilog.domain.*;
import com.skuniv.fuwarilog.dto.Post.PostResponse;
import com.skuniv.fuwarilog.dto.PostBookmark.PostBookmarkResponse;
import com.skuniv.fuwarilog.dto.PostLike.PostLikeResponse;
import com.skuniv.fuwarilog.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostBookmarkRepository postBookmarkRepository;
    private final PostLikeRepository postLikeRepository;

    /**
     * @implSpec 게시글 조회
     * @param userId 사용자 고유 번호
     * @return List<PostResponse.PostListDTO> 게시글 목록 반환
     */
    public List<PostResponse.PostListDTO> getPosts(Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.USER_NOT_FOUND));

            List<Post> post = postRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));

            return post.stream()
                    .map(PostResponse.PostListDTO::from)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BadRequestException(ErrorResponseStatus.RESPONSE_ERROR);
        }
    }

    /**
     * @implSpec 게시글 북마크 수정
     * @param userId 사용자 고유 번호
     * @param postId 게시글 고유 번호
     * @return PostBookmarkResponse.PostBookmarkStateDTO 게시글 북마크 반환
     */
    public PostBookmarkResponse.PostBookmarkStateDTO editPostBookmark(Long userId, long postId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.USER_NOT_FOUND));

            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.NOT_EXIST_POST));

            PostBookmark postBookmark = postBookmarkRepository.findByUserIdAndPostId(userId, postId);

            boolean bookmarked;

            if(postBookmark == null) {
                postBookmark = PostBookmark.builder()
                        .post(post)
                        .user(user)
                        .build();
                postBookmarkRepository.save(postBookmark);
                bookmarked = true;

            } else {
                postBookmarkRepository.delete(postBookmark);
                bookmarked = false;
            }

            return PostBookmarkResponse.PostBookmarkStateDTO.of(postId, userId, bookmarked);

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BadRequestException(ErrorResponseStatus.SAVE_DATA_ERROR);
        }
    }

    /**
     * @implSpec 게시글 좋아요 수정
     * @param userId 사용자 고유 번호
     * @param postId 게시글 고유 번호
     * @return PostLikeResponse.PostLikesStateDTO 게시글 좋아요 반환
     */
    public PostLikeResponse.PostLikesStateDTO editPostLikes(Long userId, long postId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.USER_NOT_FOUND));

            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.NOT_EXIST_POST));

            PostLike postLike = postLikeRepository.findByUserIdAndPostId(userId, postId);

            // 좋아요 상태 반환 객체
            boolean liked;

            if(postLike == null) {
                postLike = PostLike.builder()
                        .post(post)
                        .user(user)
                        .build();
                postLikeRepository.save(postLike);
                liked = true;
            } else {
                postLikeRepository.delete(postLike);
                liked = false;
            }

            return PostLikeResponse.PostLikesStateDTO.of(postId, userId, liked);

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BadRequestException(ErrorResponseStatus.SAVE_DATA_ERROR);
        }
    }
}
