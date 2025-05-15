package org.elitclass.api.model;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Builder
public class PageDto {
    private Long id;
    private String title;
    private String context;
    private Boolean isQuiz;

}
