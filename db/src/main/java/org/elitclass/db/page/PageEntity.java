package org.elitclass.db.page;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.elitclass.db.BaseEntity;
import org.elitclass.db.lecture.LectureEntity;

@Table(name= "page")
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class PageEntity extends BaseEntity {

    @Column(name = "is_quiz", nullable = false)
    private Boolean isQuiz;

    @Column(name = "title", length = 50,nullable = false)
    private String title;

    @Column(name = "context", nullable = false)
    private String context;

    @JoinColumn(name = "lecture_id", nullable = false)
    @ToString.Exclude
    @ManyToOne
    private LectureEntity lecture;
}
