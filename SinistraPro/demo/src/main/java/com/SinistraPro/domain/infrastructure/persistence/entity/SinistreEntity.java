package com.SinistraPro.domain.infrastructure.persistence.entity;

import com.SinistraPro.domain.model.StatutSinistre;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sinistres")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SinistreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String numero;

    @Column(nullable = false)
    private String typeSinistre;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDate dateIncident;

    private String lieuIncident;
    private String numeroPolicAssurance;
    private String numeroConstatAmiable;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutSinistre statut;

    @Column(nullable = false)
    private LocalDateTime dateDeclaration;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id", nullable = false)
    private UtilisateurEntity client;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "agent_id")
    private UtilisateurEntity agent;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "expert_id")
    private UtilisateurEntity expert;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "rapport_id")
    private RapportEntity rapport;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "decision_id")
    private DecisionEntity decision;

    @OneToMany(mappedBy = "sinistre", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<DocumentEntity> documents = new ArrayList<>();

    @OneToMany(mappedBy = "sinistre", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<HistoriqueEntity> historiques = new ArrayList<>();
}