package org.elitclass.db.lecture;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

import org.elitclass.db.BaseEntity;
import org.elitclass.db.classes.ClassesEntity;
import org.elitclass.db.page.PageEntity;

@Table(name= "lecture")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class LectureEntity extends BaseEntity {

    @Column(name = "lecture_title", length = 50, nullable = false)
    private String lectureTitle;

    @Column(name = "context", nullable = false)
    private String context;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    private ClassesEntity classes;

    @OneToMany(mappedBy = "lecture")
    private List<PageEntity> pageList = List.of();
}
