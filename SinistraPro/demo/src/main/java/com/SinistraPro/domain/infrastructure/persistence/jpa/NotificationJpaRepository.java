package com.SinistraPro.domain.infrastructure.persistence.jpa;

import com.SinistraPro.domain.infrastructure.persistence.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface NotificationJpaRepository extends JpaRepository<NotificationEntity, Long> {

    List<NotificationEntity> findByUtilisateurIdOrderByDateCreationDesc(Long utilisateurId);

    long countByUtilisateurIdAndLueFalse(Long utilisateurId);

    @Modifying
    @Transactional
    @Query("UPDATE NotificationEntity n SET n.lue = true WHERE n.id = :id")
    void marquerCommeLue(Long id);

    @Modifying
    @Transactional
    @Query("UPDATE NotificationEntity n SET n.lue = true WHERE n.utilisateur.id = :utilisateurId")
    void marquerToutesCommeLues(Long utilisateurId);
}