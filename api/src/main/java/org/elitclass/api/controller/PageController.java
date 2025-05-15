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
            @ApiResponse(responseCode = "4400", description = "잘못된 요청")
    })
    @GetMapping("/page/{id}")
    public Api<PageDto> pageView(@PathVariable Long id){
        return Api.OK(pageService.pageView(id));
    }

    @Operation(summary = "페이지 조회", description = "특정 페이지의 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "4200", description = "페이지 조회 성공"),
            @ApiResponse(responseCode = "4400", description = "잘못된 요청")
    })
    @GetMapping("/page/all")
    public Api<List<PageDto>> pageViewAll(){
        return Api.OK(pageService.pageViewAll());
    }
}
