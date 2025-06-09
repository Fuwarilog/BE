package com.skuniv.fuwarilog.security.jwt;

import com.skuniv.fuwarilog.config.exception.BadRequestException;
import com.skuniv.fuwarilog.config.exception.ErrorResponseStatus;
import com.skuniv.fuwarilog.domain.User;
import com.skuniv.fuwarilog.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import io.jsonwebtoken.Claims;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;


@Slf4j
@RequiredArgsConstructor
@Component
public class JwtTokenProvider {
    private final UserRepository userRepository;
    @Value("${jwt.secret}")
    private String secretKey;

    // Token 생성
    public String createToken(String email, List<String> roles, long duration) {
        Instant now = Instant.now();
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .subject(email)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(duration)))
                .claim("roles", roles)
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    // accessToken 생성
    public String createAccessToken(String email, List<String> roles) {
        return createToken(email, roles, 1000L * 60 * 60 * 24 * 7);  // 개발 기간중 시간 늘림 -> 추후 변경 예정
    }

    // refreshToken 생성
    public String createRefreshToken(String email) {
        return createToken(email, List.of(), 1000L * 60 * 60 * 24 * 7);
    }

    // 구글 로그인 후 전달된 JWT를 API 접근 시 인증처리(Header 방식)
    public Authentication getAuthentication(String token) {
        token = cleanToken(token);
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String email = claims.getSubject();
        List<String> roles = claims.get("roles", List.class);

        List<SimpleGrantedAuthority> authorities = roles != null
                ? roles.stream().map(SimpleGrantedAuthority::new).toList()
                : List.of();

        return new UsernamePasswordAuthenticationToken(email, "", authorities);
    }

    // 구글 로그인 아이디 얻기
    public Long getUserId(String token) {
        token = cleanToken(token);
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        String jwt =  Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();

        User user = userRepository.findByEmail(jwt)
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.USER_NOT_FOUND));

        return user.getId();
    }

    // 로그인 이메일 얻기
    public String getUserEmail(String token) {
        token = cleanToken(token);
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token).getPayload().getSubject();
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        token = cleanToken(token);
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private String cleanToken (String token) {
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7).trim();
        }
        return token;
    }
}