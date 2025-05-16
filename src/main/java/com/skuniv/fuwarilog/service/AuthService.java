package com.skuniv.fuwarilog.service;

import com.skuniv.fuwarilog.config.exception.BadRequestException;
import com.skuniv.fuwarilog.config.exception.ErrorResponseStatus;
import com.skuniv.fuwarilog.domain.User;
import com.skuniv.fuwarilog.dto.AuthRequest;
import com.skuniv.fuwarilog.repository.UserRepository;
import com.skuniv.fuwarilog.security.jwt.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ResponseEntity<?> registUser(HttpServletRequest request, HttpServletResponse response, AuthRequest.postRegisterDTO infoDTO) {
        // 사용자가 존재하면 예외
        if(userRepository.findByEmail(infoDTO.getEmail()).isPresent()) {
            throw new BadRequestException(ErrorResponseStatus.EXIST_USER_EMAIL);
        }

        // 신규 사용자면 등록
        String accessToken = jwtTokenProvider.createAccessToken(infoDTO.getEmail(), List.of("ROLE_USER"));
        String refreshToken = jwtTokenProvider.createRefreshToken(infoDTO.getEmail());

        // AccessToken 쿠키
        Cookie accessCookie = new Cookie("access_token", accessToken);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(60 * 60 * 24);
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(true);

        // RefreshToken 쿠키
        Cookie refreshCookie = new Cookie("refresh_token", refreshToken);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(60 * 60 * 24 * 7);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);

        // 사용자 정보 저장
        User user = User.builder()
                .name(infoDTO.getName())
                .email(infoDTO.getEmail())
                .password(passwordEncoder.encode(infoDTO.getPassword()))
                .provider(null)
                .refreshToken(refreshToken)
                .build();

        userRepository.save(user);

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

        return ResponseEntity.ok(accessCookie);
    }

    public ResponseEntity<?> loginUser(HttpServletRequest request, HttpServletResponse response, AuthRequest.postLoginDTO infoDTO) {
        User user = userRepository.findByEmail(infoDTO.getEmail())
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.USER_NOT_FOUND));

        String accesseToken = jwtTokenProvider.createAccessToken(infoDTO.getEmail(), List.of("ROLE_USER"));
        String refreshToken = jwtTokenProvider.createRefreshToken(infoDTO.getEmail());

        if (passwordEncoder.matches(infoDTO.getPassword(), user.getPassword())) {
            System.out.println("true");
        } else {
            System.out.println("false");
        }

        // AccessToken 쿠키
        Cookie accessCookie = new Cookie("access_token", accesseToken);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(60 * 60 * 24);
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(true);

        // RefreshToken 쿠키
        Cookie refreshCookie = new Cookie("refresh_token", refreshToken);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(60 * 60 * 24 * 7);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

        return ResponseEntity.ok(accessCookie);
    }


    public void invalidateCookie(String name, HttpServletResponse response) {
        Cookie cookie = new Cookie(name, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
