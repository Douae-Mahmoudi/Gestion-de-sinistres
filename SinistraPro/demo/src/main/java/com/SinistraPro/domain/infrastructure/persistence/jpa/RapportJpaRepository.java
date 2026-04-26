package com.SinistraPro.domain.infrastructure.persistence.jpa;


import com.SinistraPro.domain.infrastructure.persistence.entity.RapportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RapportJpaRepository extends JpaRepository<RapportEntity, Long> {

    @Query("SELECT r FROM RapportEntity r " +
            "JOIN SinistreEntity s ON s.rapport.id = r.id " +
            "WHERE s.id = :sinistreId")
    Optional<RapportEntity> findBySinistreId(@Param("sinistreId") Long sinistreId);
}