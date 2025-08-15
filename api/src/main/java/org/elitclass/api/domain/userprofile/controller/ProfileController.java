package org.elitclass.api.domain.userprofile.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class ProfileController {

    private final org.elitclass.api.user.service.CustomOAuth2UserService userService;




}


