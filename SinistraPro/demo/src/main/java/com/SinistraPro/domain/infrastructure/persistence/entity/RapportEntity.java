package com.SinistraPro.domain.infrastructure.persistence.entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "rapports")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RapportEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String descriptionDommages;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montantEstime;

    @Column(columnDefinition = "TEXT")
    private String observations;

    @Column(nullable = false)
    private LocalDateTime dateSoumission;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "expert_id", nullable = false)
    private UtilisateurEntity expert;
}