package com.SinistraPro.domain.infrastructure.persistence.entity;


import com.SinistraPro.domain.model.StatutSinistre;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "historiques")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HistoriqueEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private StatutSinistre ancienStatut;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutSinistre nouveauStatut;

    @Column(columnDefinition = "TEXT")
    private String commentaire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "effectue_par_id", nullable = false)
    private UtilisateurEntity effectuePar;

    @Column(nullable = false)
    private LocalDateTime dateAction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sinistre_id", nullable = false)
    private SinistreEntity sinistre;
}
