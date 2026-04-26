package com.SinistraPro.domain.port.in;

import com.SinistraPro.domain.model.Sinistre;

public interface RejeterSinistreUseCase {
    Sinistre rejeter(Long sinistreId, String motif, Long superviseurId);
}