package com.SinistraPro.domain.port.in;

import com.SinistraPro.domain.model.Rapport;
import com.SinistraPro.domain.model.Sinistre;

public interface SoumettreRapportUseCase {
    Sinistre soumettre(Long sinistreId, Rapport rapport);
}