package com.s3m.formation.api.kpi.client.repository;

import com.s3m.formation.api.kpi.client.projection.TotalFormationHoursProjection;
import com.s3m.formation.domain.sessionFormation.SessionFormation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ClientFormationKpiRepository extends JpaRepository<SessionFormation, Integer> {
    @Query("""
        SELECT COALESCE(SUM(s.dHeures), 0) AS totalHeures
        FROM SessionFormation s
    """)
    TotalFormationHoursProjection getTotalFormationHours();
}