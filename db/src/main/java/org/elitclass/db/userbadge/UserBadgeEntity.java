package org.elitclass.db.userbadge;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.elitclass.db.BaseEntity;

@Table(name= "user_badge")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class UserBadgeEntity extends BaseEntity {

    @Column(nullable = false)
    private Long badgeId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String isView;
}
