package org.elitclass.api.domain.webide.model;

import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateIdeWithJdkResponse {
    private String containerId;
    private String projectName;
}
