package org.elitclass.api.model;

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
