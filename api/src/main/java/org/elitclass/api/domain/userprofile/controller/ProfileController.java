package org.elitclass.api.domain.userprofile.controller;

import lombok.RequiredArgsConstructor;
import org.elitclass.api.domain.user.service.CustomOAuth2UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class ProfileController {

    private final CustomOAuth2UserService userService;




}


