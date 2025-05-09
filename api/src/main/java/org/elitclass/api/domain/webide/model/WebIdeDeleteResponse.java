package org.elitclass.api.domain.webide.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WebIdeDeleteResponse {

    private String containerId;

    private String message;
}
