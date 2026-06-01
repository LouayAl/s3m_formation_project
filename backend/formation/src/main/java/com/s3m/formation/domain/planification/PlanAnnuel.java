// src/main/java/com/s3m/formation/domain/planification/PlanAnnuel.java
package com.s3m.formation.domain.planification;

import com.s3m.formation.domain.entreprise.Entreprise;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "plan_annuel",
        uniqueConstraints = @UniqueConstraint(columnNames = {"annee", "id_entreprise"})
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PlanAnnuel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer annee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_entreprise", nullable = false)
    private Entreprise entreprise;

    // Monthly targets — 0 means no target set
    @Column(name = "jan", nullable = false) private Integer jan = 0;
    @Column(name = "fev", nullable = false) private Integer fev = 0;
    @Column(name = "mar", nullable = false) private Integer mar = 0;
    @Column(name = "avr", nullable = false) private Integer avr = 0;
    @Column(name = "mai", nullable = false) private Integer mai = 0;
    @Column(name = "jui", nullable = false) private Integer jui = 0;
    @Column(name = "jul", nullable = false) private Integer jul = 0;
    @Column(name = "aou", nullable = false) private Integer aou = 0;
    @Column(name = "sep", nullable = false) private Integer sep = 0;
    @Column(name = "oct", nullable = false) private Integer oct = 0;
    @Column(name = "nov", nullable = false) private Integer nov = 0;
    @Column(name = "dec", nullable = false) private Integer dec = 0;
}