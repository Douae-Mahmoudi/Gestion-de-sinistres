package com.SinistraPro.domain.port.in;

import com.SinistraPro.domain.model.Sinistre;

public interface DeclarerSinistreUseCase {
    Sinistre declarer(Sinistre sinistre);
}