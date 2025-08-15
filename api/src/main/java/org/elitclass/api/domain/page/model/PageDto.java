package org.elitclass.api.domain.page.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PageDto {
    private Long id;
    private String title;
    private String context;
    private Boolean isQuiz;

}
