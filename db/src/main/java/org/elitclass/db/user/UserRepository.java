package org.elitclass.db.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
     Optional<UserEntity> findByProviderId(String providerId);
}
