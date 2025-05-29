package org.elitclass.api.domain.webide.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.elitclass.db.usercontainer.enums.Language;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class GetWebIdeRequest {
    private Long userId;
    private Language language;
}

