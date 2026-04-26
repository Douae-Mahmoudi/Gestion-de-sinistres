package com.SinistraPro.domain.port.out;

import com.SinistraPro.domain.model.Notification;
import java.util.List;

public interface NotificationRepositoryPort {
    Notification save(Notification notification);
    List<Notification> findByUtilisateurId(Long utilisateurId);
    long countNonLuesByUtilisateurId(Long utilisateurId);
    void marquerCommeLue(Long notificationId);
    void marquerToutesCommeLues(Long utilisateurId);
}