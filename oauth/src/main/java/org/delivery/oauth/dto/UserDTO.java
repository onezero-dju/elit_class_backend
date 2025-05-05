package org.delivery.oauth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {

    private String role;
    private String email;
    private String nickname;
}
