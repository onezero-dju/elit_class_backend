package org.elitclass.db.usercontainer;

import org.elitclass.db.user.UserEntity;
import org.elitclass.db.usercontainer.enums.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface UserContainerRepository extends JpaRepository<UserContainerEntity, Long> {

    List<UserContainerEntity> findAllByUserIdAndLanguage(UserEntity userId,Language language);

    Optional<UserContainerEntity> findByUserIdAndLanguage(UserEntity userId,Language language);
    UserContainerEntity findContainerIdProjectNameByUserIdAndLanguage(UserEntity userId, Language language);
}


