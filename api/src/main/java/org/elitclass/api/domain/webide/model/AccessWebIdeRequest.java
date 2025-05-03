package org.elitclass.api.domain.webide.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class AccessWebIdeRequest {
    private Long userId;
    private Long webIdeId;
    private String language;
}
