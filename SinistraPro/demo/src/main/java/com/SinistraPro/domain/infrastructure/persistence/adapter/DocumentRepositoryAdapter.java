package com.SinistraPro.domain.infrastructure.persistence.adapter;


import com.SinistraPro.domain.model.Document;
import com.SinistraPro.domain.port.out.DocumentRepositoryPort;
import com.SinistraPro.domain.infrastructure.persistence.entity.SinistreEntity;
import com.SinistraPro.domain.infrastructure.persistence.jpa.DocumentJpaRepository;
import com.SinistraPro.domain.infrastructure.persistence.jpa.SinistreJpaRepository;
import com.SinistraPro.domain.infrastructure.persistence.mapper.DocumentMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Transactional

public class DocumentRepositoryAdapter implements DocumentRepositoryPort {

    private final DocumentJpaRepository jpaRepository;
    private final SinistreJpaRepository sinistreJpaRepository;
    private final DocumentMapper mapper;

    @Override
    public Document save(Document document, Long sinistreId) {
        SinistreEntity sinistreEntity = sinistreJpaRepository
                .findById(sinistreId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Sinistre introuvable id=" + sinistreId));

        return mapper.toDomain(
                jpaRepository.save(mapper.toEntity(document, sinistreEntity)));
    }

    @Override
    public Optional<Document> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<Document> findBySinistreId(Long sinistreId) {
        return jpaRepository.findBySinistreId(sinistreId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}