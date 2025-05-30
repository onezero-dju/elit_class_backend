package org.elitclass.api.domain.webide.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.elitclass.db.usercontainer.enums.Language;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GetWebIdeResponse {
    private String containerName;
    private String containerId;
    private String projectName;
    private Language language;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
