package org.elitclass.db.lecture;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.elitclass.db.BaseEntity;

@Table(name= "lecture")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class LectureEntity extends BaseEntity {

    @Column(length = 50, nullable = false)
    private String lectureTitle;

    @Column( nullable = false)
    private String context;

    @Column( nullable = false)
    private Long classId;
}
