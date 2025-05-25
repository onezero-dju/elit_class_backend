package org.elitclass.api.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

// 로그인
@RequiredArgsConstructor
@RestController
@RequestMapping("/test")
public class UserApiController {

    @GetMapping("/")
    @ResponseBody
    public String test() {
        return "after you login successfully";
    }

}
