package org.elitclass.api.domain.webide.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveCodeRequest {
    private String containerId;
    private String code;
    private language language;
}
