package org.elitclass.db.user;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.elitclass.db.BaseEntity;
import org.elitclass.db.likes.LikesEntity;

import java.util.List;

@Table(name= "user")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class UserEntity extends BaseEntity {

    
    @Column(length = 100,unique = true, nullable = false)
    private String email;

    @Column(length = 50, nullable = false)
    private String nickname;

    @Column(length = 100)
    private String payment;

    @Column(name = "is_certified",length = 50,nullable = false)
    private String isCertified;

    @OneToMany(mappedBy = "classes", cascade = CascadeType.REMOVE)
    private List<LikesEntity> likes = List.of();

}
