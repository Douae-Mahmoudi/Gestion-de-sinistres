package com.SinistraPro.domain.port.in;

import com.SinistraPro.domain.model.Sinistre;

import java.time.LocalDate;

public interface CloturerSinistreUseCase {
    Sinistre cloturer(Long sinistreId, String numeroVirement, LocalDate datePaiement);
}