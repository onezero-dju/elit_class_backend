package org.elitclass.api.domain.classes.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LikesViewDto {

    private int likeCheck;
    private int count;
    private String userName;
}
