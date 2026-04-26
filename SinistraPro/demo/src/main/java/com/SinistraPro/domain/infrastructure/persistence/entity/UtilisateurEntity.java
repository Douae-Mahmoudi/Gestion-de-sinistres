package com.SinistraPro.domain.infrastructure.persistence.entity;

import com.SinistraPro.domain.model.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "utilisateurs")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UtilisateurEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String motDePasse;

    private String telephone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private LocalDateTime dateCreation;
    private String resetToken;
    private LocalDateTime resetTokenExpiry;
}
