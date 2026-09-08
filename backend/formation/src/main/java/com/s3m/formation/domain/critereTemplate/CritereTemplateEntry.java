package com.s3m.formation.domain.critereTemplate;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "critere_template_entry",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"template_id", "jour", "critere_index"}
        )
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CritereTemplateEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "template_id", nullable = false)
    private CritereTemplate template;

    @Column(nullable = false)
    private Integer jour;

    @Column(name = "critere_index", nullable = false)
    private Integer critereIndex;

    @Column(nullable = false, length = 500)
    private String libelle;

    @Column(length = 255)
    private String categorie;
}