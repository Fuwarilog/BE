package com.skuniv.fuwarilog.service;

import com.skuniv.fuwarilog.domain.User;
import com.skuniv.fuwarilog.dto.AuthRequest;
import com.skuniv.fuwarilog.dto.AuthResponse;
import com.skuniv.fuwarilog.repository.UserRepository;
import com.skuniv.fuwarilog.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public AuthResponse.resRegisterDTO registUser(AuthRequest.postRegisterDTO request) {
        String token = jwtTokenProvider.createCommonToken(String.valueOf(request.getPassword()), List.of("ROLE_USER"));

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(token)
                .build();

        user = userRepository.save(user);

        return AuthResponse.resRegisterDTO.builder()
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
    }
}
