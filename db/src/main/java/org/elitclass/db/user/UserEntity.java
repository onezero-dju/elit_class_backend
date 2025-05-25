package org.elitclass.db.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.elitclass.db.BaseEntity;
import org.elitclass.db.user.enums.UserStatus;

import java.time.LocalDateTime;

@Table(name= "user")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class UserEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100,unique = true, nullable = false)
    private String email;

    @Column(length = 50, nullable = false)
    private String nickname;

    @Column(length = 100)
    private String provider;

    @Column(length = 100)
    private String providerId;



    /*
        private String statusContext;

        @Column(length = 100)
        private String payment;

        @Column(length = 50)
        private String isCertified;


        private Long classId;
    */
    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.nickname = name;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
}

