package com.s3m.formation.domain.sessionFormation;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateSessionRequest {
    @NotNull
    private Integer idEntreprise;
    @NotNull
    private Integer idFormation;

    @JsonProperty("dHeures")
    @NotNull
    private BigDecimal dHeures;

    @JsonProperty("dJours")
    @NotNull
    private BigDecimal dJours;

    @NotNull
    private LocalDate dateDebut;

    @NotNull
    private LocalDate dateFin;

    private Integer idFormateur; // optional
    private Integer idFournisseur; // optional
}
