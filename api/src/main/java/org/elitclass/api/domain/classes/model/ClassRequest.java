package org.elitclass.api.domain.classes.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;



@Schema(description = "클래스 요청 DTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ClassRequest {

    private String imageUrl;

    @Schema(description = "클래스 제목", example = "스프링 부트 강좌", required = true)
    @NotBlank(message = "클래스 제목은 필수 입력 사항입니다.")
    private String classTitle;

    @Schema(description = "클래스 설명", example = "스프링 부트의 기초부터 심화까지 배우는 강좌입니다.")
    private String description;

    private String language;

    private String version;

    
} 