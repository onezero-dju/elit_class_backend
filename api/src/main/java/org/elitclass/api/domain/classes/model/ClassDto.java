package org.elitclass.api.domain.classes.model;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.elitclass.api.domain.lecture.model.LectureDto;
import org.elitclass.db.likes.LikesEntity;

@Getter
@Setter
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
//
//    @Builder.Default
//    private List<LikesEntity> likes = List.of();



}

