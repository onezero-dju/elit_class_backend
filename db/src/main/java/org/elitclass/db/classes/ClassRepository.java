package org.elitclass.db.classes;


import org.elitclass.db.classes.enums.ClassStatus;
import org.elitclass.db.classes.model.LanguageVersionDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClassRepository extends JpaRepository<ClassEntity, Long> {
    List<ClassEntity> findAllByIdAndStatusOrderByIdDesc(Long id,ClassStatus status );
    List<LanguageVersionDTO> findLanguageVersionById(Long id);
}
