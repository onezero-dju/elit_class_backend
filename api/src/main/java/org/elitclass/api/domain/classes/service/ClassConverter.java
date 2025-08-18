package org.elitclass.api.domain.classes.service;

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

        Long userId = classEntity.getUser() != null ? classEntity.getUser().getId() : null;

        return ClassDto.builder()
                .id(classEntity.getId())
                .classTitle(classEntity.getClassTitle())
                .description(classEntity.getDescription())
                .likes(classEntity.getLikeCount())
                .views(classEntity.getViews())
                .status("등록")
                .imageUrl(classEntity.getImageUrl())
                .language(classEntity.getLanguage())
                .version(classEntity.getVersion())
                .userId(userId)
                .lectureList(lectureList)
                .build();
    }

    public ClassDto toDto(ClassesEntity e, boolean isOwner) {
        ClassDto dto = toDto(e);
        if (dto != null) dto.setOwner(isOwner);
        return dto;
    }
} 