package org.elitclass.db.lecture;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LectureRepository extends JpaRepository<LectureEntity,Long> {
    List<LectureEntity> findByClasses_IdOrderByIdAsc(Long classId);
}
