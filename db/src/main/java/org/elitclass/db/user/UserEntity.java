package org.elitclass.db.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.elitclass.db.BaseEntity;
import org.elitclass.db.user.enums.UserStatus;

@Table(name= "user")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class UserEntity extends BaseEntity {

    @Column(length = 50,nullable = false)
    private String classTitle;

    @Column(nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    private boolean isPremium;

    private Long like;

    private Long views;
}
