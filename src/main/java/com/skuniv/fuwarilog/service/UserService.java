package com.skuniv.fuwarilog.service;

import com.skuniv.fuwarilog.config.exception.BadRequestException;
import com.skuniv.fuwarilog.config.exception.ErrorResponseStatus;
import com.skuniv.fuwarilog.domain.User;
import com.skuniv.fuwarilog.dto.UserRequest;
import com.skuniv.fuwarilog.dto.UserResponse;
import com.skuniv.fuwarilog.repository.UserRepository;
import com.skuniv.fuwarilog.security.jwt.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@AllArgsConstructor
public class UserService {

    private final JwtTokenProvider jwtTokenProvider;
    private UserRepository userRepository;

    @Transactional
    public UserResponse.UserInfoDTO findUserInfo(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.USER_NOT_FOUND));

        new UserResponse.UserInfoDTO();
        return UserResponse.UserInfoDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .pictureUrl(user.getPictureUrl())
                .build();
    }

    @Transactional
    public UserResponse.UserInfoDTO editUserInfo(Long userId, UserRequest.UserInfoDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.USER_NOT_FOUND));

        if(Objects.equals(user.getProvider(), "google")) {
            user.setName(request.getName());
            user = userRepository.save(user);

        } else {
            user.setName(request.getName());
            user.setPassword(jwtTokenProvider.createToken(request.getPassword(), List.of("ROLE_USER")));
            user = userRepository.save(user);
        }

        return UserResponse.UserInfoDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .password(user.getPassword())
                .pictureUrl(user.getPictureUrl())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
