package com.s3m.formation.domain.formateur;


import com.s3m.formation.domain.entreprise.Entreprise;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "formateur")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Formateur {
    @Id
    @Column(name = "id_formateur")
    private Integer idFormateur;

    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private Boolean actif;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "id_fournisseur",
            referencedColumnName = "id_entreprise"
    )
    private Entreprise entreprise;
}
