package org.elitclass.api.domain.webide.model;

import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WebIdeRunContainerResponse {
    private String containerId;
    private String containerName;
    private String status;
}
