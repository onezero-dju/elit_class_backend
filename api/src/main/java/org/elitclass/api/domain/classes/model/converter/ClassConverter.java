package org.elitclass.api.domain.classes.model.converter;

import org.elitclass.api.domain.classes.model.ClassDto;
import org.elitclass.api.domain.lecture.service.LectureConverter;
import org.elitclass.db.classes.ClassesEntity;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ClassConverter {

    private final LectureConverter lectureConverter;

    public ClassDto toDto(ClassesEntity classEntity) {
        
        var lectureList = classEntity.getLecturesList().stream()
            .map(lectureConverter::toDto).toList();

        return ClassDto.builder()
                .id(classEntity.getId())
                .classTitle(classEntity.getClassTitle())
                .description(classEntity.getDescription())
                .likes(classEntity.getLikeCount())
                .views(classEntity.getViews())
                .status(classEntity.getStatus())
                .imageUrl(classEntity.getImageUrl())
                .lectureList(lectureList)
                .language(classEntity.getLanguage())
                .version(classEntity.getVersion())
                .build();
    }
    public ClassesEntity toEntity(ClassDto classDto) {

        return ClassesEntity.builder()
                .id(classDto.getId())
                .classTitle(classDto.getClassTitle())
                .description(classDto.getDescription())
                .likeCount(classDto.getLikes())
                .views(classDto.getViews())
                .status(classDto.getStatus())
                .imageUrl(classDto.getImageUrl())
                .language(classDto.getLanguage())
                .version(classDto.getVersion())
                .build();
    }
} 