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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@AllArgsConstructor
public class UserService {

    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private UserRepository userRepository;

    @Transactional
    public UserResponse.UserInfoDTO findUserInfo(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.USER_NOT_FOUND));

        return UserResponse.UserInfoDTO.from(user);
    }

    @Transactional
    public UserResponse.UserInfoDTO editUserInfo(Long userId, UserRequest.UserInfoDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.USER_NOT_FOUND));

        if(Objects.equals(user.getProvider(), "google")) {
            user.setName(request.getName());
            user.setPictureUrl(request.getPictureUrl());
            user = userRepository.save(user);

        } else {
            //validatePasswordStrength(request.getPassword());

            if(passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new BadRequestException(ErrorResponseStatus.INVALID_SAME_PASSWORD);
            }

            String newPassword = passwordEncoder.encode(request.getPassword());
            user.setName(request.getName());
            user.setPassword(newPassword);
            user.setPictureUrl(request.getPictureUrl());
            user = userRepository.save(user);
        }

        return UserResponse.UserInfoDTO.from(user);
    }

    private void validatePasswordStrength(String password) {
        String regex = "^(?=.*[A-Za-z])(?=.*\\\\d)(?=.*[@$!%*#?&])[A-Za-z\\\\d@$!%*#?&]{8,}$";
        if(!password.matches(regex)) {
            throw new BadRequestException(ErrorResponseStatus.INVALID_PASSWORD);
        }
    }

    public String storeProfileImage(Long userId, MultipartFile image) {
        try {
            String uploadDir = "uploads/profile/";
            File profile_dir = new File(uploadDir);
            if(!profile_dir.exists()) profile_dir.mkdir();

            String filename = userId + "_" + System.currentTimeMillis() + "_" + image.getOriginalFilename();
            Path filePath = Paths.get(uploadDir + filename);
            Files.write(filePath, image.getBytes());

            return "/static/profile/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("프로필 이미지 저장 실패", e);
        }
    }
}
