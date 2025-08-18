package org.elitclass.api.domain.lecture.model;


import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotNull(message = "class_id는 필수입니다.")
    @JsonProperty("class_id") // 스네이크 케이스 바인딩
    private Long classId;

    @NotBlank(message = "강의 제목은 필수 입력 사항입니다.")
    @JsonProperty("lecture_title")
    private String lectureTitle;

    @NotBlank(message = "강의 내용은 필수 입력 사항입니다.")
    private String context;

    @NotBlank(message = "IDE 여부는 필수 입력 사항입니다.")
    @JsonProperty("is_ide")
    @JsonAlias({"isIde"})               // 프론트가 isIde로 보내도 OK
    private Boolean isIde;
}
