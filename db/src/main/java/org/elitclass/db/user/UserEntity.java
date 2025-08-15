package org.elitclass.db.user;

import jakarta.persistence.*;
import lombok.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.elitclass.db.BaseEntity;
import org.elitclass.db.likes.LikesEntity;
import org.elitclass.db.user.enums.UserRole;

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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(length = 100,unique = true, nullable = false)
    private String email;

    @Column(length = 50, nullable = false)
    private String name;


    @Column(length = 100)
    private String provider;

    @Column(length = 100)
    @JoinColumn(name = "provider_id")
    private String providerId;

//    @Column(name = "is_certified",length = 50,nullable = false)
//    private String isCertified;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private UserRole role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    @Builder.Default
    private List<LikesEntity> likes = List.of();
}


