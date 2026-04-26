package com.SinistraPro.domain.port.out;

import com.SinistraPro.domain.model.Rapport;

import java.util.Optional;

public interface RapportRepositoryPort {
    Rapport save(Rapport rapport);
    Optional<Rapport> findById(Long id);
    Optional<Rapport> findBySinistreId(Long sinistreId);
}