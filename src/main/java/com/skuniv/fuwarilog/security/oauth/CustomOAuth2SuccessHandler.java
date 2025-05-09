package com.skuniv.fuwarilog.security.oauth;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skuniv.fuwarilog.security.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String userEmail = oAuth2User.getName(); // 구글 사용자 ID
        String jwt = jwtTokenProvider.createToken(userEmail, List.of("ROLE_USER"));

        response.setHeader("Authorization", "Bearer " + jwt);
        response.sendRedirect("/login/success?token=" + jwt);
    }
}
