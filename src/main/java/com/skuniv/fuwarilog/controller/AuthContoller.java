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
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    @Operation(summary = "일반 로그인 API", description = "이메일, 비밀번호 입력시 토큰 발급")
    public ResponseEntity<?> login(@RequestBody AuthRequest.postLoginDTO request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.USER_NOT_FOUND));
        if(Objects.equals(jwtTokenProvider.createCommonToken(request.getPassword(), List.of("ROLE_USER")), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = jwtTokenProvider.createCommonToken(String.valueOf(user.getPassword()), List.of("ROLE_USER"));
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/register")
    @Operation(summary = "일반 회원가입 API", description = "이름, 이메일, 비밀번호 입력 시 토큰 발급 및 회원등록")
    public ResponseEntity<AuthResponse.resRegisterDTO> register(@RequestBody AuthRequest.postRegisterDTO request) {
        AuthResponse.resRegisterDTO response = authService.registUser(request);
        return ResponseEntity.ok(response);
    }
}
