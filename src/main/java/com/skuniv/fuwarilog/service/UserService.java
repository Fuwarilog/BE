package com.skuniv.fuwarilog.service;

import com.skuniv.fuwarilog.config.exception.BadRequestException;
import com.skuniv.fuwarilog.config.exception.ErrorResponseStatus;
import com.skuniv.fuwarilog.domain.PostBookmark;
import com.skuniv.fuwarilog.domain.PostLike;
import com.skuniv.fuwarilog.domain.User;
import com.skuniv.fuwarilog.dto.PostBookmark.PostBookmarkResponse;
import com.skuniv.fuwarilog.dto.PostLike.PostLikeResponse;
import com.skuniv.fuwarilog.dto.User.UserRequest;
import com.skuniv.fuwarilog.dto.User.UserResponse;
import com.skuniv.fuwarilog.repository.PostBookmarkRepository;
import com.skuniv.fuwarilog.repository.PostLikeRepository;
import com.skuniv.fuwarilog.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class UserService {

    private final PostLikeRepository postLikeRepository;
    private UserRepository userRepository;
    private PostBookmarkRepository postBookmarkRepository;

    /**
     * @implSpec 사용자 정보 반환
     * @param id 사용자 고유 번호
     * @return user 사용자 정보 반환 DTO
     */
    @Transactional
    public UserResponse.UserInfoDTO findUserInfo(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.USER_NOT_FOUND));

        return UserResponse.UserInfoDTO.from(user);
    }

    /**
     * @implSpec 사용자 정보 수정
     * @param userId 사용자 고유 번호
     * @param request 사용자 정보 수정 요청 DTO
     * @return user 사용자 정보 수정 반환 DTO
     */
    @Transactional
    public UserResponse.UserInfoDTO editUserInfo(Long userId, UserRequest.UserInfoDTO request, MultipartFile image) {
        // 1. 사용자 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.USER_NOT_FOUND));

        // 2. 이미지 저장
        String imageUrl = null;
        if(image != null && !image.isEmpty()) {
            imageUrl = storeProfileImage(image);
        }

        user.setPictureUrl(imageUrl);
        user.setName(request.getName());
        user = userRepository.save(user);

        return UserResponse.UserInfoDTO.from(user);
    }

    private String storeProfileImage(MultipartFile image) {
        try {
            String uploadDir = "uploads/profile/";
            File profile_dir = new File(uploadDir);
            if(!profile_dir.exists()) profile_dir.mkdirs();

            String filename = UUID.randomUUID() + "_" + StringUtils.cleanPath(Objects.requireNonNull(image.getOriginalFilename()));
            Path filePath = Paths.get(uploadDir + filename);
            Files.write(filePath, image.getBytes());

            return ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/static/profile/")
                    .path(filename)
                    .toUriString();
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new BadRequestException(ErrorResponseStatus.SAVE_PROFILE_IMAGE_ERROR);
        }
    }

    /**
     * @implSpec 사용자 게시글 좋아요 리스트 반환
     * @param userId 사용자 고유 번호
     * @return UserResponse.UserPostLikeDTO 사용자 좋아요 게시글 리스트 반환
     */
    public UserResponse.UserPostLikeDTO getPostLikesByUser(Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.USER_NOT_FOUND));

            List<PostLike> postLikes = postLikeRepository.findAllByUser(user);

            return UserResponse.UserPostLikeDTO.builder()
                    .userId(userId)
                    .postLikes(postLikes.stream()
                            .map(postLike -> {
                                return PostLikeResponse.PostLikesStateDTO.builder()
                                        .postId(postLike.getPost().getId())
                                        .userId(postLike.getUser().getId())
                                        .liked(postLike.getPost().isLikeState())
                                        .build();
                            }).collect(Collectors.toList())
                    ).build();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BadRequestException(ErrorResponseStatus.RESPONSE_ERROR);
        }
    }

    /**
     * @implSpec 사용자 게시글 북마크 리스트 반환
     * @param userId 사용자 고유 번호
     * @return UserResponse.UserBookmarkDTO 사용자 북마크 게시글 리스트 반환
     */
    public UserResponse.UserBookmarkDTO getPostBookmarksByUser(long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.USER_NOT_FOUND));

            List<PostBookmark> postBookmarks = postBookmarkRepository.findAllByUser(user);

            return UserResponse.UserBookmarkDTO.builder()
                    .userId(userId)
                    .postBookmarks(postBookmarks.stream()
                            .map(postBookmark -> {
                                return PostBookmarkResponse.PostBookmarkStateDTO.builder()
                                        .postId(postBookmark.getPost().getId())
                                        .userId(postBookmark.getUser().getId())
                                        .bookmarked(postBookmark.getPost().isBookmarkState())
                                        .build();
                            }).collect(Collectors.toList())
                    ).build();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BadRequestException(ErrorResponseStatus.RESPONSE_ERROR);
        }
    }
}
