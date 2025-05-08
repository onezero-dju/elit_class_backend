package org.elitclass.api.model;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Schema(description = "강의 응답 DTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class LectureDto {
    private Long id;
    private String lectureTitle;
    private String context;
    private Long classId;

    private List<PageDto> pageList = List.of();
}
