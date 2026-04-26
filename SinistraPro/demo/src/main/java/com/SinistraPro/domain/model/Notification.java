package com.SinistraPro.domain.model;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    private Long id;
    private String message;
    private String type;
    private boolean lue;
    private LocalDateTime dateCreation;
    private Utilisateur utilisateur;
    private Sinistre sinistre;
}