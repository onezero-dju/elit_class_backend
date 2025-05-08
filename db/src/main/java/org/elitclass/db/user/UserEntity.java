package org.elitclass.db.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.elitclass.db.BaseEntity;

@Table(name= "user")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class UserEntity extends BaseEntity {

    
    @Column(length = 100,unique = true, nullable = false)
    private String email;

    @Column(length = 100)
    private String payment;

    @Column(name = "is_certified",length = 50,nullable = false)
    private String isCertified;

}
