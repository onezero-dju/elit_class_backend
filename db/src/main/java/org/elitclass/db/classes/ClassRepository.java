package org.elitclass.db.classes;

import org.elitclass.db.classes.enums.ClassStatus;
import org.elitclass.db.classes.model.LanguageVersionDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClassRepository extends JpaRepository<ClassesEntity, Long> {
    List<ClassesEntity> findAllByIdAndStatusOrderByIdDesc(Long id,ClassStatus status );
    List<LanguageVersionDTO> findLanguageVersionById(Long id);
    List<ClassesEntity> findTop5ByOrderByLikesDesc();

}
