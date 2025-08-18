package org.elitclass.api.domain.lecture.model;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import org.elitclass.api.domain.page.model.PageDto;

@Getter
@Builder
public class LectureDto {
    private Long id;
    private String lectureTitle;
    private String context;
    private Long classId;
    private Boolean isIde;

    @Builder.Default
    private List<PageDto> pageList = List.of();
}
