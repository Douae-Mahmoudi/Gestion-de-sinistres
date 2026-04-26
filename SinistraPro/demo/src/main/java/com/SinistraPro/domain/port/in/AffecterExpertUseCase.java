package com.SinistraPro.domain.port.in;

import com.SinistraPro.domain.model.Sinistre;
import com.SinistraPro.domain.model.Utilisateur;

public interface AffecterExpertUseCase {
    Sinistre affecter(Long sinistreId, Long expertId, String commentaireAgent, Utilisateur agentConnecte);
}