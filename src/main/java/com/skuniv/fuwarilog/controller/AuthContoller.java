package com.skuniv.fuwarilog.controller;

import com.skuniv.fuwarilog.config.exception.BadRequestException;
import com.skuniv.fuwarilog.config.exception.ErrorResponseStatus;
import com.skuniv.fuwarilog.domain.User;
import com.skuniv.fuwarilog.dto.AuthRequest;
import com.skuniv.fuwarilog.dto.AuthResponse;
import com.skuniv.fuwarilog.repository.UserRepository;
import com.skuniv.fuwarilog.security.jwt.JwtTokenProvider;
import com.skuniv.fuwarilog.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Tag(name = "Auth API", description = "인증 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/oauth2")
public class AuthContoller {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @PostMapping("/register")
    @Operation(summary = "일반 회원가입 API", description = "이름, 이메일, 비밀번호 입력 시 토큰 발급 및 회원등록")
    public ResponseEntity<?> register(HttpServletRequest request, HttpServletResponse response, @RequestBody AuthRequest.postRegisterDTO infoDTO) {
        ResponseEntity<?> userInfo = authService.registUser(request, response, infoDTO);
        return ResponseEntity.ok(userInfo);
    }

    @PostMapping("/login")
    @Operation(summary = "일반 로그인 API", description = "이메일, 비밀번호 입력시 토큰 발급")
    public ResponseEntity<?> login(HttpServletRequest request, HttpServletResponse response, @RequestBody AuthRequest.postLoginDTO infoDTO) {
        ResponseEntity<?> userInfo = authService.loginUser(request, response, infoDTO);
        return ResponseEntity.ok(userInfo);
    }

    @PostMapping("/token/refresh")
    @Operation(summary = "토큰 갱신 API", description = "토큰 재발급")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if(cookies == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No cookies found.");
        }

        String refreshToken = null;
        for (Cookie cookie : cookies) {
            if ("refresh_token".equals(cookie.getName())) {
                refreshToken = cookie.getValue();
                break;
            }
        }

        if(refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token.");
        }

        String email = jwtTokenProvider.getUserEmail(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.USER_NOT_FOUND));

        if (!refreshToken.equals(user.getRefreshToken())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("refresh token missmatch.");
        }

        String newAccessToken = jwtTokenProvider.createAccessToken(email, List.of("ROLE_USER"));

        Cookie accessTokenCookie = new Cookie("access_token", newAccessToken);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setMaxAge(60 * 60 * 24 );
        accessTokenCookie.setHttpOnly(true);

        response.addCookie(accessTokenCookie);
        return ResponseEntity.ok().build();
    }
}
