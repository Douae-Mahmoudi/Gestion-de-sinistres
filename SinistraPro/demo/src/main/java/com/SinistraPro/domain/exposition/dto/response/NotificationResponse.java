package com.SinistraPro.domain.exposition.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponse {
    private Long id;
    private String message;
    private String type;
    private boolean lue;
    private LocalDateTime dateCreation;
    private Long sinistreId;
}