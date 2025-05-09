package org.elitclass.api.domain.webide.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class WebIdeBuildResponse {
    private Long userId;
    private String containerId;
    private String error;
    private String output;
}
