package com.skuniv.fuwarilog.security.jwt;

import com.skuniv.fuwarilog.config.exception.BadRequestException;
import com.skuniv.fuwarilog.config.exception.ErrorResponseStatus;
import com.skuniv.fuwarilog.domain.User;
import com.skuniv.fuwarilog.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import io.jsonwebtoken.Claims;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Component
public class JwtTokenProvider {
    private final UserRepository userRepository;
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.token-validity-in-seconds}")
    private long validityInMilliseconds;

    public String generateToken(String userId) {
        return createToken(userId, List.of("ROLE_USER"));
    }

    // 토큰 생성
    public String createToken(String email, List<String> roles) {
        Instant now = Instant.now();
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .subject(email)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(validityInMilliseconds)))
                .claim("roles", roles)
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

//    public Authentication getAuthentication(String token) {
//        Claims claims = parseClaims(token);
//        if(claims.get("userId") == null && claims.get("roles") == null) {
//            return null;
//        }
//
//        Long userId = Long.valueOf(claims.get("userId").toString());
//        Optional<User> findUser = userRepository.findById(userId);
//        if(findUser.isEmpty()) {
//            return null;
//        }
//
//        UserDetails userDetails = new PrincipalDetails(findUser.get());
//
//        return new UsernamePasswordAuthenticationToken(userDetails, "", List.of());
//    }

    public Claims parseClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload(); // Claim 반환 확인
    }

    // 로그인 아이디 얻기
    public Long getUserId(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        String userEmail = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.INVALID_TOKEN));

        return user.getId();
    }

    // 로그인 이메일 얻기
    public String getUserEmail(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token).getPayload().getSubject();
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
