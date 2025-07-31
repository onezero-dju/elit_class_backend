package org.elitclass.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.elitclass.api.api.Api;
import org.elitclass.api.model.ClassDto;
import org.elitclass.api.model.ClassRequest;
import org.elitclass.api.service.ClassService;
import org.elitclass.api.service.S3UploaderService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.bouncycastle.asn1.x500.style.RFC4519Style.description;

@Tag(name = "Class", description = "클래스 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ClassController {
    
    private final ClassService classService;
    private final S3UploaderService s3UploaderService;

    @PostMapping(value = "/class", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "클래스 생성", description = "이미지 URL을 포함한 JSON으로 클래스를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "2200", description = "클래스 생성 성공"),
            @ApiResponse(responseCode = "2400", description = "잘못된 요청")
    })
    public ResponseEntity<ClassDto> create(
            @RequestBody @Valid ClassRequest request,
            Authentication authentication
    ) {
        ClassDto dto = classService.create(request, authentication);
        return ResponseEntity.ok(dto);
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
    public Api<Void> delete(@RequestBody Long id, Authentication authentication) {
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


    @PostMapping(value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "이미지 업로드", description = "S3에 이미지 업로드 후 URL 반환")
    public ResponseEntity<String> uploadImage(
            @Parameter(description = "이미지 파일", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            @RequestPart("image") MultipartFile image) throws IOException {

        String imageUrl = s3UploaderService.upload(image);
        return ResponseEntity.ok(imageUrl);
    }

    @GetMapping("/classes/popular")
    public ResponseEntity<List<ClassDto>> getPopularClasses() {
        return ResponseEntity.ok(classService.getPopularClasses());
    }
} 