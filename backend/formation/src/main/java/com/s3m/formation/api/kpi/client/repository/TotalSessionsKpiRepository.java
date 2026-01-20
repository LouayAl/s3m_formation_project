package com.s3m.formation.api.kpi.client.repository;

import com.s3m.formation.api.kpi.client.projection.TotalSessionsProjection;
import com.s3m.formation.domain.sessionFormation.SessionFormation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TotalSessionsKpiRepository extends JpaRepository<SessionFormation, Integer> {
    @Query("SELECT COUNT(s) AS totalSessions FROM SessionFormation s")
    TotalSessionsProjection getTotalSessions();
}
