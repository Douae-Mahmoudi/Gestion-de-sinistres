package com.SinistraPro.domain.infrastructure.persistence.jpa;


import com.SinistraPro.domain.infrastructure.persistence.entity.HistoriqueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoriqueJpaRepository extends JpaRepository<HistoriqueEntity, Long> {
    List<HistoriqueEntity> findBySinistreIdOrderByDateActionAsc(Long sinistreId);
}