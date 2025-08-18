package org.elitclass.api.domain.user.service;

import org.elitclass.api.domain.user.dto.CustomOAuth2User;
import org.elitclass.api.domain.user.dto.GoogleResponse;
import org.elitclass.api.domain.user.dto.NaverResponse;
import org.elitclass.api.domain.user.dto.OAuth2Response;
import org.elitclass.db.user.UserEntity;
import org.elitclass.db.user.UserRepository;
import org.elitclass.db.user.enums.UserRole;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomOAuth2UserService  extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }



    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // OAuth2 제공자로부터 사용자 정보를 가져옴
        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println(oAuth2User.getAttributes());

        // naver, google 인지 확인
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2Response oAuth2Response = null;

        if (registrationId.equals("naver")) {
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        }
        else if (registrationId.equals("google")) {
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        }
        else {
            return null;
        }

        String providerId = oAuth2Response.getProviderId();

        // providerId로 기존 사용자가 있는지 확인
        Optional<UserEntity> existData = userRepository.findByProviderId(providerId);
        UserEntity userEntity;

        // 기존 사용자가 존재하면 업데이트
        if (existData.isPresent()) {
            userEntity = existData.get();
            userEntity.setProvider(oAuth2Response.getProvider());
            userEntity.setProviderId(providerId);
            userEntity.setEmail(oAuth2Response.getEmail());
            userEntity.setName(oAuth2Response.getName());
            // 기존 사용자는 role을 변경하지 않음

            userRepository.save(userEntity);

        }
        // 새로운 사용자인 경우 저장
        else {
            userEntity = new UserEntity();
            userEntity.setProvider(oAuth2Response.getProvider());
            userEntity.setProviderId(providerId);
            userEntity.setEmail(oAuth2Response.getEmail());
            userEntity.setName(oAuth2Response.getName());
            userEntity.setRole(UserRole.USER); // 신규 사용자에게 USER 권한 부여

            // 새로운 사용자를 저장
            userEntity = userRepository.save(userEntity);
        }

        // CustomOAuth2User 객체를 생성하여 반환 (인증 처리에 사용)
        return new CustomOAuth2User(userEntity, oAuth2User.getAttributes());
    }
}
