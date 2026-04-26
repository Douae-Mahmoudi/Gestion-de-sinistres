package com.SinistraPro.domain.port.out;

import com.SinistraPro.domain.model.Historique;

import java.util.List;

public interface HistoriqueRepositoryPort {
    Historique save(Historique historique);
    List<Historique> findBySinistreId(Long sinistreId);
}