package org.elitclass.api.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserProfileDto {

    private Long classId;
    private String email;
    private String nickname;
}
