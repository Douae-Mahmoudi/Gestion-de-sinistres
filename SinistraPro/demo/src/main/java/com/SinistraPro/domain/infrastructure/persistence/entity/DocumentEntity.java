package com.SinistraPro.domain.infrastructure.persistence.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomFichier;

    @Column(nullable = false)
    private String cheminFichier;

    @Column(nullable = false)
    private String typeDocument;

    private Long taille;

    @Column(nullable = false)
    private LocalDateTime dateUpload;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploade_par_id", nullable = false)
    private UtilisateurEntity uploadePar;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sinistre_id", nullable = false)
    private SinistreEntity sinistre;
}
