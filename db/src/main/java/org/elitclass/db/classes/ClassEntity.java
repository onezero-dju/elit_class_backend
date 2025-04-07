package org.elitclass.db.classes;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.elitclass.db.BaseEntity;
import org.elitclass.db.classes.enums.ClassStatus;

@Table(name= "class")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class ClassEntity extends BaseEntity {

    @Column(length = 50,nullable = false)
    private String classTitle;

    @Column(nullable = false)
    private Long user;

    @Column(nullable = false)
    private Boolean isPremium;

    private Long like;

    private Long views;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ClassStatus status;

}
