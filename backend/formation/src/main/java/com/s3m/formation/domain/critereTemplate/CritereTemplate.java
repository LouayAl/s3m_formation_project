package com.s3m.formation.domain.critereTemplate;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "critere_template")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CritereTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nom;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CritereTemplateType type;
}