package com.s3m.formation.domain.sessionFormation;

import com.s3m.formation.domain.reservation.DemandeReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SessionFormationRepository
        extends JpaRepository<SessionFormation, Integer> {
    List<SessionFormation> findByFormation_IdFormation(Integer idFormation);
    boolean existsByDemande(DemandeReservation demande);

    @Query("""
        select s from SessionFormation s
        join fetch s.demande d
        join fetch d.entreprise e
        join fetch s.formation f
        left join fetch s.formateur fo
        left join fetch s.fournisseur fu
        where (:statut is null or s.statut = :statut)
          and (:formationId is null or f.idFormation = :formationId)
          and (:entrepriseId is null or e.idEntreprise = :entrepriseId)
          and (:startDate is null or s.dateDebut >= :startDate)
          and (:endDate is null or s.dateFin <= :endDate)
        order by s.dateDebut desc
    """)
    List<SessionFormation> adminSearch(
            @Param("statut") SessionFormationStatut statut,
            @Param("formationId") Integer formationId,
            @Param("entrepriseId") Integer entrepriseId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
