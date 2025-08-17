package org.elitclass.db.classes;

import org.elitclass.db.classes.enums.ClassStatus;
import org.elitclass.db.classes.model.LanguageVersionDTO;
import org.elitclass.db.user.UserEntity;
import org.elitclass.db.user.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClassRepository extends JpaRepository<ClassesEntity, Long> {
    List<ClassesEntity> findAllByIdAndStatusOrderByIdDesc(Long id,ClassStatus status );
    List<LanguageVersionDTO> findLanguageVersionById(Long id);
    List<ClassesEntity> findTop5ByStatusOrderByLikeCountDescIdDesc(ClassStatus status);
    List<ClassesEntity> findTop3ByUser_RoleAndStatusOrderByLikeCountDescIdDesc(UserRole role, ClassStatus status);
}
