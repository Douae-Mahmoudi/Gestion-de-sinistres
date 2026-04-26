package com.SinistraPro.domain.infrastructure.persistence.jpa;


import com.SinistraPro.domain.infrastructure.persistence.entity.SinistreEntity;
import com.SinistraPro.domain.model.StatutSinistre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SinistreJpaRepository extends JpaRepository<SinistreEntity, Long> {
    List<SinistreEntity> findByClientId(Long clientId);
    List<SinistreEntity> findByExpertId(Long expertId);
    List<SinistreEntity> findByAgentId(Long agentId);
    List<SinistreEntity> findByStatut(StatutSinistre statut);
    boolean existsByNumero(String numero);
}
