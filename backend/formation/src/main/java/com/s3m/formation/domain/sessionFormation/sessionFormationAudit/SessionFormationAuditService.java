package com.s3m.formation.domain.sessionFormation.sessionFormationAudit;


import com.s3m.formation.api.dto.SessionFormationAuditDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionFormationAuditService {
    private final SessionFormationAuditRepository repository;

    public List<SessionFormationAuditDto> getAuditBySession(Integer sessionId) {
        return repository
                .findBySession_IdSessionOrderByDateModificationDesc(sessionId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private SessionFormationAuditDto toDto(SessionFormationAudit audit) {
        return new SessionFormationAuditDto(
                audit.getIdAudit(),
                audit.getStatutAvant(),
                audit.getStatutApres(),
                audit.getModifiePar(),
                audit.getDateModification(),
                audit.getCommentaire()
        );
    }
}
