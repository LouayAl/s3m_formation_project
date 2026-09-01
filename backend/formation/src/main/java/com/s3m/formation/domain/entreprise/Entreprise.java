package com.s3m.formation.domain.entreprise;


import com.s3m.formation.domain.formateur.Formateur;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "entreprise")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Entreprise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_entreprise")
    private Integer idEntreprise;

    @Column(name = "nom_entreprise")
    private String nomEntreprise;

    @OneToMany(mappedBy = "entreprise", fetch = FetchType.LAZY)
    private List<Formateur> formateurs;

    @Column(name = "type_entreprise", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private TypeEntreprise typeEntreprise;

}
