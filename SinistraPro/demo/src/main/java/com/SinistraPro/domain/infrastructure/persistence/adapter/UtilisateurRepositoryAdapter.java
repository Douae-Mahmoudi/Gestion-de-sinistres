package com.SinistraPro.domain.infrastructure.persistence.adapter;

import com.SinistraPro.domain.infrastructure.persistence.entity.UtilisateurEntity;
import com.SinistraPro.domain.model.Role;
import com.SinistraPro.domain.model.Utilisateur;
import com.SinistraPro.domain.port.out.UtilisateurRepositoryPort;
import com.SinistraPro.domain.infrastructure.persistence.jpa.UtilisateurJpaRepository;
import com.SinistraPro.domain.infrastructure.persistence.mapper.UtilisateurMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UtilisateurRepositoryAdapter implements UtilisateurRepositoryPort {

    private final UtilisateurJpaRepository jpaRepository;
    private final UtilisateurMapper mapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Utilisateur save(Utilisateur utilisateur) {
        return mapper.toDomain(
                jpaRepository.save(mapper.toEntity(utilisateur)));
    }

    @Override
    public Optional<Utilisateur> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Utilisateur> findByEmail(String email) {
        return jpaRepository.findByEmail(email)
                .map(mapper::toDomain);
    }

    @Override
    public List<Utilisateur> findByRole(Role role) {
        return jpaRepository.findByRole(role).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Utilisateur> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public Optional<Utilisateur> findByResetToken(String token) {
        return jpaRepository.findByResetToken(token)
                .map(mapper::toDomain);
    }

    @Override
    public void updatePassword(String email, String nouveauMotDePasse) {
        UtilisateurEntity entity = jpaRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
        entity.setMotDePasse(passwordEncoder.encode(nouveauMotDePasse));
        jpaRepository.save(entity);
    }
}