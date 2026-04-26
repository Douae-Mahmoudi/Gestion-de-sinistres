package com.SinistraPro.domain.port.out;

import com.SinistraPro.domain.model.Decision;

import java.util.Optional;

public interface DecisionRepositoryPort {
    Decision save(Decision decision);
    Optional<Decision> findById(Long id);
    Optional<Decision> findBySinistreId(Long sinistreId);
}