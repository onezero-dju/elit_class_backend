package org.elitclass.api.domain.webide.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class WebIdeViewResponse {
    private String userId;
    private String containerId;
    private String containerName;
}
