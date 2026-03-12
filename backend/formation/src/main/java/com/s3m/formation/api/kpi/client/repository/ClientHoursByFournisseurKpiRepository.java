package com.s3m.formation.api.kpi.client.repository;

import com.s3m.formation.api.kpi.client.projection.ClientHoursByFournisseurKpiProjection;
import com.s3m.formation.domain.sessionFormation.SessionFormation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClientHoursByFournisseurKpiRepository extends JpaRepository<SessionFormation, Integer> {

    @Query("""
    SELECT 
        s.fournisseur.nomEntreprise AS fournisseur,
        SUM(s.formation.dureeHeures) AS totalHeures
    FROM Participation p
    JOIN p.session s
    WHERE s.entreprise.idEntreprise = :clientId
    GROUP BY s.fournisseur.nomEntreprise
    ORDER BY totalHeures DESC
""")
    List<ClientHoursByFournisseurKpiProjection> findByClientId(
            @Param("clientId") Integer clientId
    );

    @Query(value = """
    SELECT 
        s.id_fournisseur AS fournisseurId,
        f.nom_entreprise AS fournisseur,
        SUM(fm.d_heures) AS totalHeures
    FROM participation p
    JOIN session_formation s ON p.id_session = s.id_session
    JOIN formation fm ON s.id_formation = fm.id_formation
    JOIN entreprise f ON s.id_fournisseur = f.id_entreprise
    WHERE s.id_entreprise = :clientId
      AND EXTRACT(YEAR FROM s.date_debut)::INT = ANY(:years)
    GROUP BY s.id_fournisseur, f.nom_entreprise
    ORDER BY totalHeures DESC
""", nativeQuery = true)
    List<ClientHoursByFournisseurKpiProjection> findByClientIdAndYears(
            @Param("clientId") Integer clientId,
            @Param("years") Integer[] years
    );
}
