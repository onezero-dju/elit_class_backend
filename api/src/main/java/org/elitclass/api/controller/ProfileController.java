package org.elitclass.api.controller;

import lombok.RequiredArgsConstructor;
import org.elitclass.api.model.UserProfileDto;
import org.elitclass.api.service.ProfileService;
import org.elitclass.db.user.UserEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class ProfileController {

    private final org.elitclass.api.user.service.CustomOAuth2UserService userService;

    @GetMapping("/")
    public ResponseEntity<UserProfileDto> mypage(@AuthenticationPrincipal OAuth2User user) {
        if(privider == null){
            throw new IllegalArgumentException("Oauth provider information not found");
        }


    }

}
