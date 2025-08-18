package org.elitclass.api.domain.webide.controller;

import lombok.extern.slf4j.Slf4j;
import org.elitclass.api.api.Api;
import org.elitclass.api.domain.webide.model.*;
import org.elitclass.api.domain.webide.service.WebIdeService;
import org.elitclass.api.error.ErrorCode;
import org.elitclass.db.usercontainer.enums.Language;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/ide")
public class WebIdeController {

    private final WebIdeService webIdeService;

    public WebIdeController(WebIdeService webIdeService) {
        this.webIdeService = webIdeService;

    }

    //컨테이너 생성
    @PostMapping("/create")
    public Api<CreateIdeWithJdkResponse> create(@RequestBody CreateIdeWithJdkRequest request) {
        CreateIdeWithJdkResponse response=webIdeService.createIdeWithJDK (request);

        return Api.OK(response);
    }
    @GetMapping("/get")
    public Api<List<GetWebIdeResponse>> get(@RequestParam Long userId, Language language){

        List<GetWebIdeResponse> response = webIdeService.getWebIde(userId,language);
        return Api.OK(response);
    }

    //컨테이너 삭제
    @PostMapping("/delete")
    public Api<DeleteIdeResponse> delete(@RequestBody DeleteIdeRequest request) {

        DeleteIdeResponse response = webIdeService.deleteIde(request);

        return Api.OK(response);
    }


//    @PostMapping("/build")
//    public Api<WebIdeBuildResponse> build(
//            @Valid
//            @RequestBody String containerId
//    ){
//        var response = webIdeService.buildIde(containerId);
//        return Api.OK(response);
//    }

    @PostMapping("/down-file")
    public Api<Object> saveTree(@RequestBody FileUploadRequest request) {
        try{
            System.out.println(request.projectName());
            System.out.println(request.containerId());
            webIdeService.saveFileTreeToContainer(request);
            return Api.OK(request);
        } catch (IOException e) {
            return Api.ERROR(ErrorCode.SERVER_ERROR,"코드데이터 저장 실패");
        }
    }
}
