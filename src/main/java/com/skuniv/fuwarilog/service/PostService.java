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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostBookmarkRepository postBookmarkRepository;
    private final PostLikeRepository postLikeRepository;
    private final DiaryContentRepository diaryContentRepository;
    private final PostViewRepository postViewRepository;

    /**
     * @implSpec 게시글 조회
     * @param userId 사용자 고유 번호
     * @return List<PostResponse.PostListDTO> 게시글 목록 반환
     */
    public List<PostResponse.PostListDTO> getPosts(Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.USER_NOT_FOUND));

            List<Post> postList = postRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));

            return postList.stream()
                    .map(post -> PostResponse.PostListDTO.builder()
                            .id(post.getId())
                            .title(post.getDiaryList().getDiary().getTitle())
                            .userId(post.getDiaryList().getDiary().getTrip().getUser().getId())
                            .userName(post.getDiaryList().getDiary().getTrip().getUser().getName())
                            .date(post.getDiaryList().getDate())
                            .likesCount(post.getLikesCount())
                            .watchCount(post.getWatchCount())
                            .createdDate(post.getCreatedAt())
                            .updatedDate(post.getUpdatedAt())
                            .build())
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BadRequestException(ErrorResponseStatus.RESPONSE_ERROR);
        }
    }

    /**
     * @implSpec 특정 게시글 조회
     * @param userId 사용자 고유 번호
     * @param postId 게시글 고유 번호
     * @return PostResponse.PostListDTO 특정 게시글 내용 반환
     */
    public PostResponse.PostInfoDTO getPostContent(long userId, long postId, String ipAddress) {
        // 1. 조회수 증가 처리
        increasePostView(postId, userId, ipAddress);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.NOT_EXIST_POST));

        PostBookmark postBookmark = postBookmarkRepository.findByUserIdAndPostId(userId, postId);
        PostLike postLike = postLikeRepository.findByUserIdAndPostId(userId, postId);

        Optional<DiaryContent> content = diaryContentRepository.findByDiaryListId(post.getDiaryList().getId());
        if (content.isEmpty()) {
            throw new BadRequestException(ErrorResponseStatus.NOT_EXIST_DIARYCONTENT);
        }

        boolean bookmarkState = postBookmarkState(postBookmark);
        boolean likeState = postLikeState(postLike);

        return PostResponse.PostInfoDTO.builder()
                .id(post.getId())
                .userId(post.getDiaryList().getDiary().getTrip().getUser().getId())
                .userName(post.getDiaryList().getDiary().getTrip().getUser().getName())
                .title(post.getDiaryList().getDiary().getTitle())
                .diaryListId(post.getDiaryList().getId())
                .content(content.get().getContent())
                .date(post.getDiaryList().getDate())
                .likesCount(post.getLikesCount())
                .likeState(likeState)
                .watchCount(post.getWatchCount())
                .bookmarkState(bookmarkState)
                .createdDate(post.getCreatedAt())
                .updatedDate(post.getUpdatedAt())
                .build();

    }

    /**
     * @implSpec 조회수 증가
     * @param userId 사용자 고유 번호
     * @param postId 게시글 고유 번호
     * @param ipAddress 접속자 ip
     */
    private void increasePostView(Long postId, Long userId, String ipAddress) {
        LocalDate today = LocalDate.now();

        boolean hasViewed;

        if (userId != null) {
            hasViewed = postViewRepository.existsByUserIdAndPostIdAndViewDate(userId, postId, today);
        } else {
            hasViewed = postViewRepository.existsByIpAddressAndPostIdAndViewDate(ipAddress, postId, today);
        }

        if (!hasViewed) {
            // PostView 저장
            PostView postView = new PostView();
            postView.setPostId(postId);
            postView.setViewDate(today);
            if (userId != null) {
                postView.setUserId(userId);
            } else {
                postView.setIpAddress(ipAddress);
            }
            postViewRepository.save(postView);

            // Post 조회수 증가
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.NOT_EXIST_POST));
            post.incrementWatchCount();
            postRepository.save(post);
        }
    }

    // 북마크 상태 반환
    private boolean postBookmarkState(PostBookmark postBookmark) {
        if (postBookmark == null) {
            return false;
        } else {
            return true;
        }
    }

    // 좋아요 상태 반환
    private boolean postLikeState(PostLike postLike) {
        if (postLike == null) {
            return false;
        } else {
            return true;
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

            boolean bookmarked = false;

            if(postBookmark == null) {
                postBookmark = PostBookmark.builder()
                        .post(post)
                        .user(user)
                        .build();
                postBookmarkRepository.save(postBookmark);
                bookmarked = true;

                post.setBookmarkState(bookmarked);
                postRepository.save(post);

            } else {
                postBookmarkRepository.delete(postBookmark);

                post.setBookmarkState(bookmarked);
                postRepository.save(post);
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
    public PostLikeResponse.PostLikesStateDTO editPostLikes(Long userId, Long postId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.USER_NOT_FOUND));

            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.NOT_EXIST_POST));

            PostLike postLike = postLikeRepository.findByUserIdAndPostId(userId, postId);

            // 좋아요 상태 반환 객체
            boolean liked = false;

            if(postLike == null) {
                postLike = PostLike.builder()
                        .post(post)
                        .user(user)
                        .build();
                postLikeRepository.save(postLike);
                liked = true;

                post.setLikesCount(post.getLikesCount()+1);
                postRepository.save(post);
            } else {
                postLikeRepository.delete(postLike);

                post.setLikesCount(post.getLikesCount()-1);
                postRepository.save(post);
            }

            return PostLikeResponse.PostLikesStateDTO.of(postId, userId, liked);

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BadRequestException(ErrorResponseStatus.SAVE_DATA_ERROR);
        }
    }
}
