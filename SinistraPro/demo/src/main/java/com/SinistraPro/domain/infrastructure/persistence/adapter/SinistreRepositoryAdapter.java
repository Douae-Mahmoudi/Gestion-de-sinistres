package com.SinistraPro.domain.infrastructure.persistence.adapter;


import com.SinistraPro.domain.model.Sinistre;
import com.SinistraPro.domain.model.StatutSinistre;
import com.SinistraPro.domain.port.out.SinistreRepositoryPort;
import com.SinistraPro.domain.infrastructure.persistence.jpa.SinistreJpaRepository;
import com.SinistraPro.domain.infrastructure.persistence.mapper.SinistreMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Transactional

public class SinistreRepositoryAdapter implements SinistreRepositoryPort {

    private final SinistreJpaRepository jpaRepository;
    private final SinistreMapper mapper;

    @Override
    public Sinistre save(Sinistre sinistre) {
        return mapper.toDomain(
                jpaRepository.save(mapper.toEntity(sinistre)));
    }

    @Override
    public Optional<Sinistre> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<Sinistre> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Sinistre> findByClientId(Long clientId) {
        return jpaRepository.findByClientId(clientId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Sinistre> findByStatut(StatutSinistre statut) {
        return jpaRepository.findByStatut(statut).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Sinistre> findByExpertId(Long expertId) {
        return jpaRepository.findByExpertId(expertId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Sinistre> findByAgentId(Long agentId) {
        return jpaRepository.findByAgentId(agentId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}