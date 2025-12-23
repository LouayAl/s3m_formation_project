package com.s3m.formation.api.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
public class PlanifierDemandeRequest {
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String commentaireAdmin;
}
