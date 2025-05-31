package org.elitclass.api.user.service;

import org.elitclass.api.dto.CustomOAuth2User;
import org.elitclass.api.dto.GoogleResponse;
import org.elitclass.api.dto.NaverResponse;
import org.elitclass.api.dto.OAuth2Response;
import org.elitclass.db.user.UserEntity;
import org.elitclass.db.user.UserRepository;
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

        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println(oAuth2User.getAttributes());

        // naver, google 인지 확인하는 변수
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2Response oAuth2Response = null;

        // naver와 google에서 보내주는 인증 데이터 규격이 다름.
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

        Optional<UserEntity> existData = userRepository.findByProviderId(providerId);

        UserEntity userEntity;

        // 처음 로그인하는 경우
        if (existData.isPresent()) {
            userEntity = existData.get();

            userEntity.setProvider(oAuth2Response.getProvider());
            userEntity.setProviderId(providerId);
            userEntity.setEmail(oAuth2Response.getEmail());
            userEntity.setNickname(oAuth2Response.getName());

            userRepository.save(userEntity);

        } // 이전 값이 있는 경우
        else {
            userEntity = new UserEntity();
            userEntity.setProvider(oAuth2Response.getProvider());
            userEntity.setProviderId(providerId);
            userEntity.setEmail(oAuth2Response.getEmail());
            userEntity.setNickname(oAuth2Response.getName());

            userRepository.save(userEntity);

        }


        return new CustomOAuth2User(oAuth2Response, userEntity.getProvider());
    }

}
