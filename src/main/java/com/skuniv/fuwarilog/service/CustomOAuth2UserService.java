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

        // 제공자 정보
        String provider = userRequest.getClientRegistration().getRegistrationId();

        // 사용자 정보
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String profile_image = oAuth2User.getAttribute("profile_image_url"); // 실행x

        // DB에 유저 없으면 저장
        userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(User.builder()
                        .email(email)
                        .name(name)
                        .provider(provider)
                        .pictureUrl(profile_image)
                        .password(null)  // 비밀번호는 의미 없는 값 또는 null로 저장한다.
                        .build()));

        return oAuth2User;
    }
}
