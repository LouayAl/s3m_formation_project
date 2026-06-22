package com.s3m.formation.domain.sessionFormation;

import com.s3m.formation.domain.reservation.DemandeReservation;
import com.s3m.formation.domain.formation.Formation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
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

    @Query("SELECT MIN(s.formation.annee) FROM SessionFormation s WHERE s.fournisseur.idEntreprise = :clientId")
    Integer findPremiereAnnee(@Param("clientId") Integer clientId);

    @Query("SELECT MAX(s.formation.annee) FROM SessionFormation s WHERE s.fournisseur.idEntreprise = :clientId")
    Integer findDerniereAnnee(@Param("clientId") Integer clientId);

    @Query("SELECT MIN(s.dateDebut) FROM SessionFormation s WHERE s.fournisseur.idEntreprise = :clientId")
    LocalDate findDatePremiereFormation(@Param("clientId") Integer clientId);

    @Query("SELECT MAX(s.dateFin) FROM SessionFormation s WHERE s.fournisseur.idEntreprise = :clientId")
    LocalDate findDateDerniereFormation(@Param("clientId") Integer clientId);

    @Query("SELECT s.fournisseur.nomEntreprise FROM SessionFormation s WHERE s.fournisseur.idEntreprise = :clientId")
    String findNomClient(@Param("clientId") Integer clientId);

    List<SessionFormation> findByDemande_Entreprise_IdEntreprise(Integer idEntreprise);

    @Query("""
        SELECT COUNT(s)
        FROM SessionFormation s
        WHERE s.formation.idFormation = :idFormation
          AND EXTRACT(YEAR FROM s.dateDebut) = :year
          AND s.dHeures = :heures
    """)
    long countByModuleAndYearAndHeures(
            @Param("idFormation") Integer idFormation,
            @Param("year") int year,
            @Param("heures") BigDecimal heures
    );
    boolean existsByEntreprise_IdEntreprise(Integer idEntreprise);

    // Fetch all sessions with entities to avoid lazy-loading issues
    @Query("""
        SELECT DISTINCT s
        FROM SessionFormation s
        LEFT JOIN FETCH s.participations p
        LEFT JOIN FETCH p.employe
        """)
    List<SessionFormation> findAllWithParticipants();

    // Search/filter sessions by formation, entreprise, statut, date, etc.
    @Query("""
        SELECT s 
        FROM SessionFormation s
        JOIN FETCH s.formation f
        LEFT JOIN FETCH s.formateur fo
        LEFT JOIN FETCH s.entreprise e
        LEFT JOIN FETCH s.fournisseur fu
        WHERE (:statut IS NULL OR s.statut = :statut)
          AND (:formationId IS NULL OR f.idFormation = :formationId)
          AND (:entrepriseId IS NULL OR e.idEntreprise = :entrepriseId)
          AND (:startDate IS NULL OR s.dateDebut >= :startDate)
          AND (:endDate IS NULL OR s.dateFin <= :endDate)
        ORDER BY s.dateDebut DESC
    """)
    List<SessionFormation> search(
            @Param("statut") SessionFormationStatut statut,
            @Param("formationId") Integer formationId,
            @Param("entrepriseId") Integer entrepriseId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    boolean existsByFormation_IdFormation(Integer idFormation);
    boolean existsByReferenceSession(String referenceSession);
    List<SessionFormation> findByStatut(SessionFormationStatut statut);
    List<SessionFormation> findByFormateur_IdFormateur(Integer idFormateur);

    List<SessionFormation> findAllByEntreprise_IdEntreprise(Integer entrepriseId);

    @Query("""
        SELECT DISTINCT f
        FROM SessionFormation s
        JOIN s.formation f
        WHERE s.entreprise.idEntreprise = :entrepriseId
        ORDER BY f.module
    """)
    List<Formation> findDistinctFormationsByEntrepriseId(@Param("entrepriseId") Integer entrepriseId);


    @Query("""
        SELECT s FROM SessionFormation s
        WHERE s.entreprise.idEntreprise = :entrepriseId
          AND s.dateDebut >= :start
          AND s.dateDebut <= :end
        ORDER BY s.dateDebut ASC
    """)
    List<SessionFormation> findTermineesForEntrepriseAndYear(
            @Param("entrepriseId") Integer entrepriseId,
            @Param("start")        LocalDate start,
            @Param("end")          LocalDate end
    );
    List<SessionFormation> findByEntreprise_IdEntreprise(Integer entrepriseId);
    boolean existsByReferenceSessionAndIdSessionNot(String referenceSession, Integer idSession);


}
