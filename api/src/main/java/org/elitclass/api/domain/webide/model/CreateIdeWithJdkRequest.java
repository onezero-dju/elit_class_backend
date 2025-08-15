package org.elitclass.api.domain.webide.model;

import lombok.*;
import org.elitclass.db.usercontainer.enums.Language;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateIdeWithJdkRequest {
    private Long classId;
    private String projectName;
    private Long userId;
    private Language language;
}
