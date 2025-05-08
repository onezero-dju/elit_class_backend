package org.elitclass.api.service;

import org.elitclass.api.model.PageDto;
import org.elitclass.db.page.PageEntity;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PageConverter {
    public PageDto toDto(PageEntity pageEntity) {
        return PageDto.builder()
                .id(pageEntity.getId())
                .title(pageEntity.getTitle())
                .context(pageEntity.getContext())
                .isQuiz(pageEntity.getIsQuiz())
                .build();
    }
}
