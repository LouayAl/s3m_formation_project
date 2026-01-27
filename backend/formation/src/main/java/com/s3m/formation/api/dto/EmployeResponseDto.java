package com.s3m.formation.api.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class EmployeResponseDto {
    private Integer idEmploye;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
}
