package org.elitclass.api.domain.webide.controller;

import jakarta.validation.Valid;
import org.elitclass.api.api.Api;
import org.elitclass.api.domain.webide.model.*;
import org.elitclass.api.domain.webide.service.WebIdeService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ide")
public class WebIdeController {

    private final WebIdeService webIdeService;

    public WebIdeController(WebIdeService webIdeService) {
        this.webIdeService = webIdeService;
    }

    //컨테이너 생성
    @PostMapping("/create")
    public Api<WebIdeCreateResponse> create(@RequestBody Long userId) {
        WebIdeCreateResponse response=webIdeService.createIdeWithJDK(userId);

        return Api.OK(response);
    }

    //컨테이너 삭제
    @PostMapping("/delete")
    public Api<WebIdeDeleteResponse> delete(@RequestBody String containerId) {

        WebIdeDeleteResponse response = webIdeService.deleteIde(containerId);

        return Api.OK(response);
    }
    @PostMapping("/run")
    public Api<WebIdeRunContainerResponse> run(@RequestBody String containerId) {
        WebIdeRunContainerResponse response = webIdeService.runIdeWithContainerId(containerId);

        return Api.OK(response);
    }
    @PostMapping("/save-code")
    public Api<SaveCodeResponse> saveCode(
            @Valid
            @RequestBody SaveCodeRequest request
    ) {
        var response = webIdeService.saveCode(request);
        return Api.OK(response);
    }
    @PostMapping("/build")
    public Api<WebIdeBuildResponse> build(
            @Valid
            @RequestBody String containerId
    ){
        var response = webIdeService.buildIde(containerId);
        return Api.OK(response);
    }
}
