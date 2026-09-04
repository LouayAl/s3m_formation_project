package com.s3m.formation.auth.model;

import com.s3m.formation.domain.departement.Departement;
import com.s3m.formation.domain.entreprise.Entreprise;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false)
    private String nom;

    @Column(unique = true, nullable = false)
    private String username;

    // 🔗 MANY USERS → ONE ENTREPRISE
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "id_entreprise",
            referencedColumnName = "id_entreprise",
            nullable = false
    )
    private Entreprise entreprise;

    // 🔗 MANY USERS → ONE DEPARTEMENT (nullable — null means Admin / unscoped)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "id_departement",
            referencedColumnName = "id_departement",
            nullable = true
    )
    private Departement departement;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}