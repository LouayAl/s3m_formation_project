package com.s3m.formation.domain.departement;

import com.s3m.formation.domain.employe.Employe;
import com.s3m.formation.domain.entreprise.Entreprise;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "departement", uniqueConstraints = @UniqueConstraint(columnNames = {"id_entreprise", "nom"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Departement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_departement")
    private Integer id;

    @Column(nullable = false)
    private String nom;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_entreprise")
    private Entreprise entreprise;

    @OneToMany(mappedBy = "departement")
    private List<Employe> employes;
}
