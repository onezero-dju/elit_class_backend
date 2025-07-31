package org.elitclass.api.model;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ClassDto {
    private Long id;
    private String classTitle;
    private String description;
    private Long likes;
    private Long views;
    private String status;
    private String imageUrl;

    @Builder.Default
    private List<LectureDto> lectureList = List.of();
}

