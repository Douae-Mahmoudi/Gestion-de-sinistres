package com.SinistraPro.domain.port.in;

import com.SinistraPro.domain.model.Sinistre;

import java.math.BigDecimal;

public interface ApprouverSinistreUseCase {
    Sinistre approuver(Long sinistreId, BigDecimal montantFinal, String motif, Long superviseurId);
}