package org.elitclass.db.classes;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

import org.elitclass.db.BaseEntity;
import org.elitclass.db.classes.enums.ClassStatus;
import org.elitclass.db.lecture.LectureEntity;
import org.elitclass.db.likes.LikesEntity;
import org.elitclass.db.user.UserEntity;

@Table(name= "classes")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@ToString
@SuperBuilder
public class ClassesEntity extends BaseEntity {


    @Column(name = "class_title", length = 50,nullable = false)
    private String classTitle;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "is_premium",nullable = false)
    private Boolean isPremium;

    @Column(name = "like_count",nullable = false)
    private int likeCount = 0;

    @Column(name = "views",nullable = false)
    private Long views;

    @Column(name = "description")
    private String description;

    @Column(name = "status",nullable = false)
    @Enumerated(EnumType.STRING)
    private ClassStatus status;

    @OneToMany(mappedBy = "classes", cascade = CascadeType.REMOVE)
    private List<LikesEntity> likes = List.of();

    @OneToMany(mappedBy = "classes")
    private List<LectureEntity> lecturesList = List.of();

}
