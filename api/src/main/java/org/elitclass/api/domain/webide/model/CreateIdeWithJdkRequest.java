package org.elitclass.api.domain.webide.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.elitclass.db.usercontainer.enums.Language;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateIdeWithJdkRequest {
    String projectName;
    Long userId;
    Language language;
}
