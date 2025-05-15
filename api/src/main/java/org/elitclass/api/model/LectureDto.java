package org.elitclass.api.model;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LectureDto {
    private Long id;
    private String lectureTitle;
    private String context;
    private Long classId;

    private List<PageDto> pageList = List.of();
}
