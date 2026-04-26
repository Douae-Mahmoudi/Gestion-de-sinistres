package com.SinistraPro.domain.infrastructure.persistence.adapter;


import com.SinistraPro.domain.model.Historique;
import com.SinistraPro.domain.port.out.HistoriqueRepositoryPort;
import com.SinistraPro.domain.infrastructure.persistence.entity.SinistreEntity;
import com.SinistraPro.domain.infrastructure.persistence.jpa.HistoriqueJpaRepository;
import com.SinistraPro.domain.infrastructure.persistence.jpa.SinistreJpaRepository;
import com.SinistraPro.domain.infrastructure.persistence.mapper.HistoriqueMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class HistoriqueRepositoryAdapter implements HistoriqueRepositoryPort {

    private final HistoriqueJpaRepository jpaRepository;
    private final SinistreJpaRepository sinistreJpaRepository;
    private final HistoriqueMapper mapper;

    @Override
    public Historique save(Historique historique) {
        SinistreEntity sinistreEntity = sinistreJpaRepository
                .findById(historique.getSinistreId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Sinistre introuvable id=" + historique.getSinistreId()));

        return mapper.toDomain(
                jpaRepository.save(mapper.toEntity(historique, sinistreEntity)));
    }

    @Override
    public List<Historique> findBySinistreId(Long sinistreId) {
        return jpaRepository
                .findBySinistreIdOrderByDateActionAsc(sinistreId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
