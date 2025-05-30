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
    private String provider;

    @Column(length = 100)
    @JoinColumn(name = "provider_id")
    private String providerId;

//    @Column(name = "is_certified",length = 50,nullable = false)
//    private String isCertified;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    @Builder.Default
    private List<LikesEntity> likes = List.of();
}



    /*
        private String statusContext;

        @Column(length = 100)
        private String payment;

    @Column(length = 50,nullable = false)
    private String isCertified;

    @Column( nullable = false)
    private Long classId;
}
