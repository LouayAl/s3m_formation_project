package com.s3m.formation.api.kpi.client.repository;

import com.s3m.formation.api.kpi.client.projection.RepartitionItemProjection;
import com.s3m.formation.domain.employe.Employe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClientPopulationKpiRepository extends JpaRepository<Employe, Integer> {

    @Query("""
        SELECT COALESCE(e.csp, 'Non renseigné') AS label, COUNT(e) AS count
        FROM Employe e
        WHERE e.entreprise.idEntreprise = :clientId
        GROUP BY COALESCE(e.csp, 'Non renseigné')
    """)
    List<RepartitionItemProjection> countByCsp(@Param("clientId") Integer clientId);

    @Query("""
        SELECT COALESCE(e.fonction, 'Non renseigné') AS label, COUNT(e) AS count
        FROM Employe e
        WHERE e.entreprise.idEntreprise = :clientId
        GROUP BY COALESCE(e.fonction, 'Non renseigné')
    """)
    List<RepartitionItemProjection> countByFonction(@Param("clientId") Integer clientId);

    @Query("""
        SELECT COALESCE(e.typeContrat, 'Non renseigné') AS label, COUNT(e) AS count
        FROM Employe e
        WHERE e.entreprise.idEntreprise = :clientId
        GROUP BY COALESCE(e.typeContrat, 'Non renseigné')
    """)
    List<RepartitionItemProjection> countByTypeContrat(@Param("clientId") Integer clientId);

    @Query("""
        SELECT COALESCE(e.f_h, 'Non renseigné') AS label, COUNT(e) AS count
        FROM Employe e
        WHERE e.entreprise.idEntreprise = :clientId
        GROUP BY COALESCE(e.f_h, 'Non renseigné')
    """)
    List<RepartitionItemProjection> countByGenre(@Param("clientId") Integer clientId);
}
