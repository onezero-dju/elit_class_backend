package org.elitclass.db.likes;

import org.elitclass.db.classes.ClassesEntity;
import org.elitclass.db.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikesRepository extends JpaRepository<LikesEntity, Long> {
    void deleteByUserAndClasses(UserEntity user, ClassesEntity classes);
    boolean existsByUserAndClasses(UserEntity user, ClassesEntity classes);

}
