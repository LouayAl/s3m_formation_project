package com.s3m.formation.api.kpi.client.repository;

import com.s3m.formation.api.kpi.client.projection.ClientIdentiteKpiProjection;
import com.s3m.formation.domain.sessionFormation.SessionFormation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClientIdentiteKpiRepository extends JpaRepository<SessionFormation, Integer> {

    @Query("""
        SELECT
            s.fournisseur.nomEntreprise AS nomClient,
            MIN(s.formation.annee) AS premiereAnnee,
            MAX(s.formation.annee) AS derniereAnnee,
            MIN(s.dateDebut) AS datePremiere,
            MAX(s.dateFin) AS dateDerniere
        FROM SessionFormation s
        WHERE s.fournisseur.idEntreprise = :clientId
        GROUP BY s.fournisseur.nomEntreprise
    """)
    ClientIdentiteKpiProjection computeIdentite(@Param("clientId") Integer clientId);
}
