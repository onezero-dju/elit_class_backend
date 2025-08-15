package org.elitclass.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {

    private Long id;
    private String role;
    private String email;
    private String nickname;
    private String providerId;
    private String profileImage;
}
