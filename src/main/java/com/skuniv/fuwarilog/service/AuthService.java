package com.skuniv.fuwarilog.service;

import com.skuniv.fuwarilog.config.exception.BadRequestException;
import com.skuniv.fuwarilog.config.exception.ErrorResponseStatus;
import com.skuniv.fuwarilog.domain.User;
import com.skuniv.fuwarilog.dto.AuthRequest;
import com.skuniv.fuwarilog.dto.AuthResponse;
import com.skuniv.fuwarilog.repository.UserRepository;
import com.skuniv.fuwarilog.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse.resRegisterDTO registUser(AuthRequest.postRegisterDTO request) {
        // 사용자가 존재하면 예외
        if(userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException(ErrorResponseStatus.EXIST_USER_EMAIL);
        }

        // 신규 사용자면 등록
        String token = jwtTokenProvider.createToken(String.valueOf(request.getPassword()), List.of("ROLE_USER"));

        User user2 = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .provider(null)
                .password(token)
                .build();

        user2 = userRepository.save(user2);

        return AuthResponse.resRegisterDTO.builder()
                .userId(user2.getId())
                .name(user2.getName())
                .email(user2.getEmail())
                .password(user2.getPassword())
                .build();
    }

    public String loginUser(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.USER_NOT_FOUND));

        String token = jwtTokenProvider.createToken(password, List.of("ROLE_USER"));

        if (!passwordEncoder.matches(token, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return token;
    }
}
