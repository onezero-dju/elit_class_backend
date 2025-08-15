package org.elitclass.api.domain.classes.model;

import lombok.*;

@Setter
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Data
public class LikesDto {

    private String message;

    private int likeCount;


}
