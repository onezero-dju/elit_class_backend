package org.elitclass.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.elitclass.api.api.Api;
import org.elitclass.api.model.LectureDto;
import org.elitclass.api.model.LectureRequest;
import org.elitclass.api.service.LectureService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Lecture", description = "강의 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LectureController {

    private final LectureService lectureService;

    @Operation(summary = "강의 생성", description = "새로운 강의를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "3200", description = "강의 생성 성공"),
            @ApiResponse(responseCode = "3404", description = "잘못된 요청")
    })
    @GetMapping("/lecture")
    public Api<LectureDto> create(@Valid @RequestBody LectureRequest request) {
        return Api.OK(lectureService.createLecture(request));
    }

    @Operation(summary = "강의 조회", description = "특정 강의의 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "3200", description = "강의 조회 성공"),
            @ApiResponse(responseCode = "3404", description = "강의를 찾을 수 없음")
    })
    @GetMapping("/lecture/{id}")
    public Api<LectureDto> lectureView(@PathVariable Long id) {
        return Api.OK(lectureService.lectureView(id));
    }

    @Operation(summary = "전체 강의 조회", description = "전체 강의의 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "3200", description = "강의 목록 조회 성공"),
            @ApiResponse(responseCode = "3404", description = "강의를 찾을 수 없음")
    })
    @GetMapping("/lecture/all")
    public Api<List<LectureDto>> lectureViewAll() {
        return Api.OK(lectureService.lectureViewAll());
    }

}
