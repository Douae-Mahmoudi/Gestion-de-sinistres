package com.SinistraPro.domain.infrastructure.persistence.jpa;


import com.SinistraPro.domain.infrastructure.persistence.entity.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentJpaRepository extends JpaRepository<DocumentEntity, Long> {
    List<DocumentEntity> findBySinistreId(Long sinistreId);
}