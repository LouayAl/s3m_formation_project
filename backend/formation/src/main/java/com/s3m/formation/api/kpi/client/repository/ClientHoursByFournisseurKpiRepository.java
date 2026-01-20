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
            f.nomEntreprise AS fournisseur,
            SUM(s.dHeures) AS totalHeures
        FROM SessionFormation s
        JOIN s.fournisseur f
        WHERE s.entreprise.idEntreprise = :clientId
        GROUP BY f.nomEntreprise
        ORDER BY totalHeures DESC
    """)
    List<ClientHoursByFournisseurKpiProjection> findByClientId(
            @Param("clientId") Integer clientId
    );

}
