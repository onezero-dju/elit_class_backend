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

    public LectureDto toDto(LectureEntity e) {
        var pageList = e.getPageList().stream()
                .map(pageConverter::toDto)
                .toList();

        return LectureDto.builder()
                .id(e.getId())
                .lectureTitle(e.getLectureTitle())
                .context(e.getContext())
                .pageList(pageList)
                .isIde(e.getIsIde())
                .classId(e.getClasses() != null ? e.getClasses().getId() : null) // ← 여기 중요
                .build();
    }
}