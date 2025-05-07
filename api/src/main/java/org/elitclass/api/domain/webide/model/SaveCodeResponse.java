package org.elitclass.api.domain.webide.model;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class SaveCodeResponse {
    private String code;
    private String message;
}
