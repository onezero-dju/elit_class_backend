package org.elitclass.api.domain.lecture.service;

import org.elitclass.api.domain.lecture.model.LectureDto;
import org.elitclass.api.domain.page.service.PageConverter;
import org.elitclass.db.lecture.LectureEntity;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LectureConverter {
    
    private final PageConverter pageConverter;

    public LectureDto toDto(LectureEntity lectureEntity) {
        
        var pageList = lectureEntity.getPageList().stream()
            .map(pageConverter::toDto).toList();
            
        return LectureDto.builder()
                .id(lectureEntity.getId())
                .lectureTitle(lectureEntity.getLectureTitle())
                .context(lectureEntity.getContext())
                .pageList(pageList)
                .classId(lectureEntity.getId())
                .build();
    }
}
