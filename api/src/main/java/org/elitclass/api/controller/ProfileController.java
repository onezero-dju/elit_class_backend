package org.elitclass.api.controller;

import lombok.RequiredArgsConstructor;
import org.elitclass.api.service.ProfileService;
import org.elitclass.db.user.UserEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/")
    public String mypage(UserEntity user) {
        return user.toString();
    }

}
