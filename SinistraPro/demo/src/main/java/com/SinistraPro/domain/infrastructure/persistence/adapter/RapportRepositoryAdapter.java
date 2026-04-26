package com.SinistraPro.domain.infrastructure.persistence.adapter;

import com.SinistraPro.domain.model.Rapport;
import com.SinistraPro.domain.port.out.RapportRepositoryPort;
import com.SinistraPro.domain.infrastructure.persistence.jpa.RapportJpaRepository;
import com.SinistraPro.domain.infrastructure.persistence.mapper.RapportMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RapportRepositoryAdapter implements RapportRepositoryPort {

    private final RapportJpaRepository jpaRepository;
    private final RapportMapper mapper;

    @Override
    public Rapport save(Rapport rapport) {
        return mapper.toDomain(
                jpaRepository.save(mapper.toEntity(rapport)));
    }

    @Override
    public Optional<Rapport> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Rapport> findBySinistreId(Long sinistreId) {
        return jpaRepository.findBySinistreId(sinistreId)
                .map(mapper::toDomain);
    }
}
