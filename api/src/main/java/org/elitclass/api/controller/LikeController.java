package org.elitclass.api.controller;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.elitclass.api.api.Api;
import org.elitclass.api.model.LikesDto;
import org.elitclass.api.service.LikesService;
import org.elitclass.db.user.UserEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Transactional
public class LikeController {
    private final LikesService likesService;

    @PostMapping("/like/user/{userId}/class/{classId}")
    public ResponseEntity<LikesDto> classLike(@PathVariable Long classId, @PathVariable Long userId) {
        try {
            LikesDto result = likesService.addLike(classId, userId);
            return ResponseEntity.ok(result);
        }catch (IllegalArgumentException e) {
            LikesDto errorDto = new LikesDto();
            errorDto.setMessage(e.getMessage());
            errorDto.setLikeCount(-1);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDto);
        }catch (Exception e) {
            LikesDto errorDto = new LikesDto();
            errorDto.setMessage("서버 오류가 발생했습니다.");
            errorDto.setLikeCount(-1);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDto);
        }

    }

}

