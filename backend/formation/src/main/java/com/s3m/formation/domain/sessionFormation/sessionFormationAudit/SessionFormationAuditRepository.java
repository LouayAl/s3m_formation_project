package com.s3m.formation.domain.sessionFormation.sessionFormationAudit;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessionFormationAuditRepository extends JpaRepository<SessionFormationAudit, Long> {

    List<SessionFormationAudit> findBySession_IdSessionOrderByDateModificationDesc(Integer sessionId);

}
