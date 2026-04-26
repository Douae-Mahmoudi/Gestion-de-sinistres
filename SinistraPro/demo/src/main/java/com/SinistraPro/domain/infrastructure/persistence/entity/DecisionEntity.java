package com.SinistraPro.domain.infrastructure.persistence.entity;


import com.SinistraPro.domain.model.StatutDecision;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "decisions")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DecisionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(precision = 10, scale = 2)
    private BigDecimal montantFinal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutDecision statut;

    @Column(columnDefinition = "TEXT")
    private String motif;

    @Column(nullable = false)
    private LocalDateTime dateDecision;

    private String numeroVirement;
    private LocalDate datePaiement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "superviseur_id", nullable = false)
    private UtilisateurEntity superviseur;
}
