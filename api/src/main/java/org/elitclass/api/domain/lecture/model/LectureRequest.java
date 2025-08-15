package org.elitclass.api.domain.lecture.model;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Schema(description = "강의 요청 DTO")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class LectureRequest {
    @NotBlank(message = "강의 제목은 필수 입력 사항입니다.")
    private String lectureTitle;
    @NotBlank(message = "강의 내용은 필수 입력 사항입니다.")
    private String context;
    
    
}
