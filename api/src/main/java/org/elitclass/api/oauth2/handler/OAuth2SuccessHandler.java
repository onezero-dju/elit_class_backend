/*
package org.elitclass.api.oauth2.handler;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elitclass.api.cookie.CookieUtil;
import org.elitclass.api.dto.CustomOAuth2User;
import org.elitclass.api.jwt.JwtProvider;
import org.elitclass.db.user.UserEntity;
import org.elitclass.db.usertoken.UserTokenEntity;
import org.elitclass.db.usertoken.UserTokenRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final UserTokenRepository userTokenRepository;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        log.info("OAuth2SuccessHandler 실행됨.");

// CustomOAuth2UserService에서 반환한 CustomOAuth2User 객체에서 UserEntity 정보를 가져옴
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        UserEntity user = customOAuth2User.getUserEntity();

        Long userId = user.getId();
        String email = user.getEmail();
        String role = String.valueOf(user.getRole());

// JWT 토큰 생성
        String refreshToken = jwtProvider.createRefreshToken(userId);
        String accessToken = jwtProvider.createAccessToken(userId, email, role);

// 리프레시 토큰 저장 또는 갱신
        Optional<UserTokenEntity> optionalToken = userTokenRepository.findByUser(user);

        UserTokenEntity userToken;
        if (optionalToken.isPresent()) {
            userToken = optionalToken.get();
            userToken.setRefreshToken(refreshToken);
        } else {
            userToken = UserTokenEntity.builder()
                    .user(user)
                    .refreshToken(refreshToken)
                    .build();
        }
        userTokenRepository.save(userToken);

// JWT를 쿠키에 담아 응답
        CookieUtil.addJwtCookie(response, "accessToken", accessToken, 60 * 30);
        CookieUtil.addJwtCookie(response, "refreshToken", refreshToken, 60 * 60 * 24 * 30);

        response.sendRedirect("http://localhost:8080");
    }
}*/


package org.elitclass.api.oauth2.handler;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elitclass.api.cookie.CookieUtil;
import org.elitclass.api.dto.CustomOAuth2User;
import org.elitclass.api.jwt.JwtProvider;
import org.elitclass.db.user.UserEntity;
import org.elitclass.db.usertoken.UserTokenEntity;
import org.elitclass.db.usertoken.UserTokenRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final UserTokenRepository userTokenRepository;


    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        UserEntity user = customOAuth2User.getUserEntity();

        Long userId = user.getId();
        String email = user.getEmail();
        String role = String.valueOf(user.getRole());

        String refreshToken = jwtProvider.createRefreshToken(userId);
        String accessToken  = jwtProvider.createAccessToken(userId, email, role);

        userTokenRepository.findByUser(user).ifPresentOrElse(
                t -> { t.setRefreshToken(refreshToken); userTokenRepository.save(t); },
                () -> userTokenRepository.save(UserTokenEntity.builder().user(user).refreshToken(refreshToken).build())
        );


        CookieUtil.addJwtCookie(response, "accessToken",  accessToken,  60 * 30);           // 30분
        CookieUtil.addJwtCookie(response, "refreshToken", refreshToken, 60 * 60 * 24 * 30); // 30일

        response.sendRedirect("http://localhost:5173/");


//        // URL 인코딩
//        String at = java.net.URLEncoder.encode(accessToken, java.nio.charset.StandardCharsets.UTF_8);
//        String rt = java.net.URLEncoder.encode(refreshToken, java.nio.charset.StandardCharsets.UTF_8);
//
//        // HTML 파일로 리다이렉트 (localhost:8080/callback.html)
//        response.sendRedirect("http://localhost:8080/login/callback#at=" + at + "&rt=" + rt);
    }
}