package org.elitclass.api.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

// 비로그인 페이지
@RequiredArgsConstructor
@RestController
@RequestMapping("/open-api")
public class UserOpenApiController {

    @GetMapping("/")
    @ResponseBody
    public String openAPI() {

        return "main route";
    }

}
