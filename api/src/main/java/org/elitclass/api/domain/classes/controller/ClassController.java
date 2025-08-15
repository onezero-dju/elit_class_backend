package org.elitclass.api.domain.classes.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elitclass.api.api.Api;
import org.elitclass.api.domain.classes.model.ClassDto;
import org.elitclass.api.domain.classes.model.ClassRequest;
import org.elitclass.api.domain.classes.service.ClassService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Slf4j
@Tag(name = "Class", description = "클래스 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ClassController {
    
    private final ClassService classService;

    @Operation(summary = "클래스 생성", description = "새로운 클래스를 생성합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "2200", description = "클래스 생성 성공"),
        @ApiResponse(responseCode = "2400", description = "잘못된 요청")
    })
    @PostMapping("/class")
    public Api<ClassDto> create(@Valid @RequestBody ClassRequest request, Authentication authentication) {
        log.info("📦 요청 도착: {}", request); // 확인
        log.info("🧪 classTitle: {}", request.getClassTitle());
        log.info("🧪 imageUrl: {}", request.getImageUrl());
        return Api.OK(classService.create(request, authentication));
    }

    @Operation(summary = "클래스 조회", description = "특정 클래스의 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "2200", description = "클래스 조회 성공"),
        @ApiResponse(responseCode = "2404", description = "클래스를 찾을 수 없음")
    })
    @GetMapping("/class/{id}")
    public Api<ClassDto> view(@PathVariable Long id) {
        return Api.OK(classService.view(id));
    }

    @Operation(summary = "전체 클래스 조회", description = "모든 클래스 목록을 조회합니다.")
    @ApiResponse(responseCode = "2200", description = "클래스 목록 조회 성공")
    @GetMapping("/class/all")
    public Api<List<ClassDto>> viewAll() {
        return Api.OK(classService.viewAll());
    }

    @Operation(summary = "클래스 수정", description = "기존 클래스의 정보를 수정합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "2200", description = "클래스 수정 성공"),
        @ApiResponse(responseCode = "2404", description = "클래스를 찾을 수 없음"),
        @ApiResponse(responseCode = "2400", description = "잘못된 요청")
    })
    @PutMapping("/class/correction/{id}")
    public Api<ClassDto> update(@PathVariable Long id, @RequestBody ClassRequest request, Authentication authentication) {
        return Api.OK(classService.update(id, request, authentication));
    }

    @Operation(summary = "클래스 삭제", description = "특정 클래스를 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "2200", description = "클래스 삭제 성공"),
        @ApiResponse(responseCode = "2404", description = "클래스를 찾을 수 없음")
    })
    @DeleteMapping("/class/delete/{id}")
    public Api<Void> delete(@PathVariable Long id, Authentication authentication) {
        classService.delete(id, authentication);
        return Api.OK(null);
    }

    @Operation(summary = "클래스 신고", description = "특정 클래스를 신고합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "2200", description = "클래스 신고 성공"),
        @ApiResponse(responseCode = "2404", description = "클래스를 찾을 수 없음")
    })
    @PostMapping("/report/class/{id}")
    public Api<Void> report(@PathVariable Long id) {
        classService.report(id);
        return Api.OK(null);
    }

    @GetMapping("/class/popular")
    public Api<List<ClassDto>> popular() {
        return  Api.OK(classService.popular());
    }
} 