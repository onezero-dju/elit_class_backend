package org.elitclass.api.domain.webide.model;

import lombok.*;
import org.springframework.context.annotation.Bean;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WebIdeCreateResponse {
    private String containerId;
    private Long userId;
    private String containerName;
    private String projectName;
}
