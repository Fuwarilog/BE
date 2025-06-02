package com.skuniv.fuwarilog.security.oauth;

import com.skuniv.fuwarilog.config.exception.BadRequestException;
import com.skuniv.fuwarilog.config.exception.ErrorResponseStatus;
import com.skuniv.fuwarilog.domain.User;
import com.skuniv.fuwarilog.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import com.skuniv.fuwarilog.security.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler  extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        // accessToken, RefreshToken 생성
        String accessToken = jwtTokenProvider.createAccessToken(email, List.of("ROLE_USER"));
        String refreshToken = jwtTokenProvider.createRefreshToken(email);

        // AccessToken 쿠키
        Cookie accessCookie = new Cookie("access_token", accessToken);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(60 * 60 * 24 * 7); // 개발 기간중 시간 늘림 -> 추후 변경 예정
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(true);

        // RefreshToken 쿠키
        Cookie refreshCookie = new Cookie("refresh_token", refreshToken);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(60 * 60 * 24 * 7);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.USER_NOT_FOUND));
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);
        getRedirectStrategy().sendRedirect(request, response, "http://localhost:3000/oauth2/redirect");
    }
}
