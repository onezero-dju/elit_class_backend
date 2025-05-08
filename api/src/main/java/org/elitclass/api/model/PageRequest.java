package org.elitclass.api.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class PageRequest {

    @NotBlank(message = "페이지 제목은 필수 입력 사항입니다")
    private String title;

    @NotBlank(message = "페이지 내용은 필수 입력 사항입니다")
    private String context;

    @NotNull(message = "퀴즈 여부는 필수 입력 사항입니다")
    private Boolean isQuiz;

    
}
