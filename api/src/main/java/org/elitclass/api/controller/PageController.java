package org.elitclass.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.elitclass.api.api.Api;
import org.elitclass.api.model.PageDto;
import org.elitclass.api.model.PageRequest;
import org.elitclass.api.service.PageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Page", description = "페이지 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PageController {

    private final PageService pageService;

    @Operation(summary = "페이지 생성", description = "새로운 페이지를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "4200", description = "페이지 조회 성공"),
            @ApiResponse(responseCode = "4400", description = "잘못된 요청")
    })
    @PostMapping("/page")
    public Api<PageDto> pageCreate(@Valid @RequestBody PageRequest request){
        return Api.OK(pageService.pageCreate(request));
    }

    @Operation(summary = "페이지 조회", description = "특정 페이지의 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "4200", description = "페이지 조회 성공"),
            @ApiResponse(responseCode = "4400", description = "페이지를 찾을 수 없음")
    })
    @GetMapping("/page/{id}")
    public Api<PageDto> pageView(@PathVariable Long id){
        return Api.OK(pageService.pageView(id));
    }

    @Operation(summary = "페이지 조회", description = "특정 페이지의 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "4200", description = "페이지 조회 성공")
    })
    @GetMapping("/page/all")
    public Api<List<PageDto>> pageViewAll(){
        return Api.OK(pageService.pageViewAll());
    }

    @Operation(summary = "페이지 수정", description = "특정 페이지의 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "4200", description = "페이지 수정 성공"),
            @ApiResponse(responseCode = "4404", description = "페이지를 찾을 수 없음"),
            @ApiResponse(responseCode = "4400", description = "잘못된 요청")
    })
    @PutMapping("/page/correction/{id}")
    public Api<PageDto> pageUpdate(@PathVariable Long id, @Valid @RequestBody PageRequest request){
        return Api.OK(pageService.pageUpdate(id,request));
    }

    @Operation(summary = "페이지 삭제", description = "특정 페이지 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "4200", description = "페이지 삭제 성공"),
            @ApiResponse(responseCode = "4404", description = "페이지를 찾을 수 없음")
    })
    @DeleteMapping("/page/delete/{id}")
    public Api<Void> pageDelete(@PathVariable Long id){
        pageService.pageDelete(id);
        return Api.OK(null);
    }

//    @Operation(summary = "페이지 신고", description = "특정 페이지를 신고합니다.")
//    @ApiResponses(value = {
//        @ApiResponse(responseCode = "4200", description = "페이지 신고 성공"),
//        @ApiResponse(responseCode = "4404", description = "페이지를 찾을 수 없음")
//    })
//    @PostMapping("/report/page/{id}")
//    public Api<Void> pageReport(@PathVariable Long id){
//        pageService.pageReport(id);
//        return Api.OK(null);
//    }
}

