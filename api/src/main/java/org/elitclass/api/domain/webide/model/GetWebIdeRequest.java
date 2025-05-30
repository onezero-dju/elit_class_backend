package org.elitclass.api.domain.webide.model;

import lombok.*;
import org.elitclass.db.usercontainer.enums.Language;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Data
public class GetWebIdeRequest {
    private Long userId;
    private Language language;
}

