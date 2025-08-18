package org.elitclass.api.domain.classes.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.elitclass.api.api.Api;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/image")
public class UploadImage {

    @Operation(summary = "이미지 업로드", description = "이미지를 로컬 uploads에 저장하고 URL 반환")
    @PostMapping(path = "/uploads", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Api<String> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "파일이 비어 있습니다.");
        }
        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미지 파일만 업로드 가능합니다.");
        }

        try {
            // 프로젝트 실행 디렉터리 하위 uploads
            Path uploadDir = Paths.get(System.getProperty("user.dir"), "uploads").toAbsolutePath();
            Files.createDirectories(uploadDir);

            // 안전한 파일명
            String original = file.getOriginalFilename() == null ? "image" : file.getOriginalFilename();
            String ext = original.lastIndexOf('.') >= 0 ? original.substring(original.lastIndexOf('.')) : "";
            String savedName = UUID.randomUUID() + ext.replaceAll("[^\\.a-zA-Z0-9_-]", "");
            Path dest = uploadDir.resolve(savedName).normalize();

            // 저장
            Files.copy(file.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);

            // 프론트엔드에 넘길 URL (상대 경로 권장)
            String imageUrl = "http://localhost:8080/uploads/" + savedName;
            log.info("업로드 완료: {}", imageUrl);
            return Api.OK(imageUrl);

        } catch (IOException e) {
            log.error("이미지 저장 실패", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 저장 실패", e);
        }
    }
}