package com.skuniv.fuwarilog.service;

import com.skuniv.fuwarilog.config.exception.BadRequestException;
import com.skuniv.fuwarilog.config.exception.ErrorResponseStatus;
import com.skuniv.fuwarilog.domain.User;
import com.skuniv.fuwarilog.dto.UserRequest;
import com.skuniv.fuwarilog.dto.UserResponse;
import com.skuniv.fuwarilog.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class UserService {

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

        User userInfo = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .pictureUrl(request.getPictureUrl())
                .build();

        User infoDTO = userRepository.save(userInfo);

        return UserResponse.UserInfoDTO.builder()
                .id(infoDTO.getId())
                .name(infoDTO.getName())
                .email(infoDTO.getEmail())
                .password(infoDTO.getPassword())
                .pictureUrl(infoDTO.getPictureUrl())
                .createdAt(infoDTO.getCreatedAt())
                .updatedAt(infoDTO.getUpdatedAt())
                .build();
    }
}
