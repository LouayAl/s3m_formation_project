package com.s3m.formation.domain.sessionFormation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateSessionRequest {
    private Integer idEntreprise;
    private Integer idFormation;
    @JsonProperty("dHeures")
    private BigDecimal dHeures;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Integer idFormateur; // optional
    private Integer idFournisseur; // optional
}
