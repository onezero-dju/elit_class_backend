package org.elitclass.db.usertoken;

import jakarta.persistence.*;
import lombok.*;
import org.elitclass.db.user.UserEntity;

@Getter
@Setter
@Builder
@Entity(name = "user_token")
@NoArgsConstructor
@AllArgsConstructor
public class UserTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private UserEntity user;

    private String refreshToken;
}
