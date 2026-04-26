package com.SinistraPro.domain.infrastructure.persistence.jpa;


import com.SinistraPro.domain.infrastructure.persistence.entity.DecisionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DecisionJpaRepository extends JpaRepository<DecisionEntity, Long> {

    @Query("SELECT d FROM DecisionEntity d " +
            "JOIN SinistreEntity s ON s.decision.id = d.id " +
            "WHERE s.id = :sinistreId")
    Optional<DecisionEntity> findBySinistreId(@Param("sinistreId") Long sinistreId);
}
