package org.elitclass.api.service;

import org.elitclass.api.model.ClassDto;
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
                .isPremiun(classEntity.getIsPremium())
                .likes(classEntity.getLikes())
                .views(classEntity.getViews())
                .status("등록")
                .lectureList(lectureList)
                .build();
    }
} 