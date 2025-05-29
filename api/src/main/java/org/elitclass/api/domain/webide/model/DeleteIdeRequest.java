package org.elitclass.api.domain.webide.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.elitclass.db.usercontainer.enums.Language;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteIdeRequest {
    Long userId;
    Language language;
}
