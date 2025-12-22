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
    @Column(name = "id_entreprise")
    private Integer idEntreprise;

    @Column(name = "nom_entreprise")
    private String nomEntreprise;

    @OneToMany(mappedBy = "entreprise")
    private List<Formateur> formateurs;
}
