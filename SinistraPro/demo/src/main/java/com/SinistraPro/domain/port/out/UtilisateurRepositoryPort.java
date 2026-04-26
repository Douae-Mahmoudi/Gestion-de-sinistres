package com.SinistraPro.domain.port.out;

import com.SinistraPro.domain.model.Utilisateur;
import com.SinistraPro.domain.model.Role;

import java.util.List;
import java.util.Optional;

public interface UtilisateurRepositoryPort {
    Utilisateur save(Utilisateur utilisateur);
    Optional<Utilisateur> findById(Long id);
    Optional<Utilisateur> findByEmail(String email);
    List<Utilisateur> findByRole(Role role);
    boolean existsByEmail(String email);
    Optional<Utilisateur> findByResetToken(String token);
    void updatePassword(String email, String nouveauMotDePasse);

    List<Utilisateur> findAll();
}