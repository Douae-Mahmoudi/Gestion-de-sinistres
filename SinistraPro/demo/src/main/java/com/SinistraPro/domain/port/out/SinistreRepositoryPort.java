package com.SinistraPro.domain.port.out;

import com.SinistraPro.domain.model.Sinistre;
import com.SinistraPro.domain.model.StatutSinistre;

import java.util.List;
import java.util.Optional;

public interface SinistreRepositoryPort {
    Sinistre save(Sinistre sinistre);
    Optional<Sinistre> findById(Long id);
    List<Sinistre> findAll();
    List<Sinistre> findByClientId(Long clientId);
    List<Sinistre> findByStatut(StatutSinistre statut);
    List<Sinistre> findByExpertId(Long expertId);
    List<Sinistre> findByAgentId(Long agentId);
    boolean existsById(Long id);
    void deleteById(Long id);
}