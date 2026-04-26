package com.SinistraPro.domain.infrastructure.persistence.adapter;


import com.SinistraPro.domain.model.Decision;
import com.SinistraPro.domain.port.out.DecisionRepositoryPort;
import com.SinistraPro.domain.infrastructure.persistence.jpa.DecisionJpaRepository;
import com.SinistraPro.domain.infrastructure.persistence.mapper.DecisionMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Transactional

public class DecisionRepositoryAdapter implements DecisionRepositoryPort {

    private final DecisionJpaRepository jpaRepository;
    private final DecisionMapper mapper;

    @Override
    public Decision save(Decision decision) {
        return mapper.toDomain(
                jpaRepository.save(mapper.toEntity(decision)));
    }

    @Override
    public Optional<Decision> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Decision> findBySinistreId(Long sinistreId) {
        return jpaRepository.findBySinistreId(sinistreId)
                .map(mapper::toDomain);
    }
}