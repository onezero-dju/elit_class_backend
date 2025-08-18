package org.elitclass.api.domain.classes.model;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.elitclass.api.domain.lecture.model.LectureDto;
import org.elitclass.db.classes.enums.ClassStatus;
import org.elitclass.db.likes.LikesEntity;
import org.elitclass.db.usercontainer.enums.Language;

@Getter
@Setter
@Builder
public class ClassDto {

    private Long id;

    private String classTitle;

    private String description;

    private Long likes;

    private Long views;

    private ClassStatus status;

    private String imageUrl;

    private String  language;

    private String version;

    @Builder.Default
    private List<LectureDto> lectureList = List.of();

}

