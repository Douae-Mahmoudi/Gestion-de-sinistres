package com.SinistraPro.domain.infrastructure.persistence.jpa;


import com.SinistraPro.domain.infrastructure.persistence.entity.UtilisateurEntity;
import com.SinistraPro.domain.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UtilisateurJpaRepository extends JpaRepository<UtilisateurEntity, Long> {
    Optional<UtilisateurEntity> findByEmail(String email);
    List<UtilisateurEntity> findByRole(Role role);
    boolean existsByEmail(String email);
    Optional<UtilisateurEntity> findByResetToken(String token);
}
