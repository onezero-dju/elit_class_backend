package org.elitclass.db.classes;

import org.elitclass.db.classes.enums.ClassStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClassRepository extends JpaRepository<ClassEntity, Long> {
    List<ClassEntity> findAllByIdAndStatusOrderByIdDesc(Long id,ClassStatus status );
}
