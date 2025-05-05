package org.delivery.oauth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MyController {

    @GetMapping("/open-api/auth/login")
    @ResponseBody
    public String myAPI() {

        return "my route";
    }


}
