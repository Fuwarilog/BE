package com.skuniv.fuwarilog.service;

import com.skuniv.fuwarilog.domain.User;
import com.skuniv.fuwarilog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 제공자 정보 추출
        String provider = userRequest.getClientRegistration().getRegistrationId();

        // 사용자 정보 추출
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String picture = oAuth2User.getAttribute("picture");

        // google AccessToken 추출
        String access_token = userRequest.getAccessToken().getTokenValue();

        // DB에 유저 없으면 저장
        User user = userRepository.findByEmail(email).orElseGet(() -> userRepository.save(User.builder()
                        .email(email)
                        .name(name)
                        .provider(provider)
                        .pictureUrl(picture)
                        .googleAccessToken(access_token)
                        .build()));

        // 로그인마다 토큰 갱신
        user.setGoogleAccessToken(access_token);
        userRepository.save(user);

        return oAuth2User;
    }
}
