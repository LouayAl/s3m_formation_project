package com.s3m.formation.api.kpi.client.repository;

import com.s3m.formation.api.kpi.client.projection.*;
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

    @Query("""
        SELECT
            COALESCE(d.nom, 'Non défini') AS departement,
            COALESCE(e.f_h, 'N') AS genre,
            COUNT(p.idParticipation) AS nombre
        FROM Participation p
        JOIN p.employe e
        LEFT JOIN e.departement d
        GROUP BY d.nom, e.f_h
        ORDER BY d.nom, e.f_h
    """)
    List<ClientGenderByDepartmentKpiProjection> getGenderByDepartmentForAllEntreprises();

    @Query("""
        SELECT
            COALESCE(e.f_h, 'N') AS label,
            SUM(s.dHeures) AS totalHeures,
            COUNT(DISTINCT e.idEmploye) AS nombreEmployes
        FROM Participation p
        JOIN p.employe e
        JOIN p.session s
        GROUP BY e.f_h
    """)
    List<GenderHoursKpiProjection> getTrainingHoursByGender();

    @Query("""
        SELECT
            CASE
                WHEN e.csp IS NULL OR e.csp = '' OR e.csp = '#REF!' THEN 'Non défini'
                ELSE e.csp
            END AS csp,
            SUM(s.dHeures) AS totalHeures,
            COUNT(DISTINCT e.idEmploye) AS nombreEmployes
        FROM Participation p
        JOIN p.employe e
        JOIN p.session s
        GROUP BY
            CASE
                WHEN e.csp IS NULL OR e.csp = '' OR e.csp = '#REF!' THEN 'Non défini'
                ELSE e.csp
            END
    """)
    List<CspHoursKpiProjection> getTrainingHoursByCsp();

    @Query("""
        SELECT COUNT(DISTINCT p.employe.idEmploye) AS totalParticipants
        FROM Participation p
    """)
    TotalParticipantsKpiProjection getTotalParticipants();

}
