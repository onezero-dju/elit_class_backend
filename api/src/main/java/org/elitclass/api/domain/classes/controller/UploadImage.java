package org.elitclass.api.domain.classes.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.elitclass.api.api.Api;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
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

    @Operation(summary = "이미지 업로드", description = "이미지를 로컬 static/uploads에 저장하고 URL 반환")
    @PostMapping("/upload")
    public Api<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // 절대 경로로 저장
            String uploadDirPath = System.getProperty("user.dir") + "/uploads";
            File uploadDir = new File(uploadDirPath);
            if (!uploadDir.exists()) uploadDir.mkdirs();

            // 저장할 파일 이름 지정
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDirPath, fileName);

            // 파일 저장
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 프론트에 전달할 이미지 URL
            String imageUrl = "http://localhost:8080/uploads/" + fileName;
            log.info("업로드 완료: {}", imageUrl);
            return Api.OK(imageUrl);

        } catch (IOException e) {
            log.error("이미지 저장 실패", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 저장 실패", e);
        }
    }
}
