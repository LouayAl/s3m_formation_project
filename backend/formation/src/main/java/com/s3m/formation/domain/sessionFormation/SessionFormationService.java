package com.s3m.formation.domain.sessionFormation;


import com.s3m.formation.api.dto.SessionResponseDto;
import com.s3m.formation.domain.sessionFormation.sessionFormationAudit.SessionFormationAudit;
import com.s3m.formation.domain.sessionFormation.sessionFormationAudit.SessionFormationAuditRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SessionFormationService {

    private final SessionFormationRepository repository;
    private final SessionFormationAuditRepository auditRepository;


    public List<SessionResponseDto> getSessionsByFormation(Integer formationId) {
        return repository.findByFormation_IdFormation(formationId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public SessionResponseDto getSession(Integer sessionId) {
        return repository.findById(sessionId)
                .map(this::toDto)
                .orElse(null);
    }

     /* =========================
       TRANSITIONS
       ========================= */

    public void demarrerSession(Integer sessionId) {
        SessionFormation session = getSessionOrThrow(sessionId);
        SessionFormationStatut avant = session.getStatut();

        session.demarrer(LocalDate.now());
        auditTransition(session, avant, session.getStatut());

    }

    public void terminerSession(Integer sessionId) {
        SessionFormation session = getSessionOrThrow(sessionId);
        SessionFormationStatut avant = session.getStatut();

        session.terminer();
        auditTransition(session, avant, session.getStatut());

    }

    public void annulerSession(Integer sessionId) {
        SessionFormation session = getSessionOrThrow(sessionId);
        SessionFormationStatut avant = session.getStatut();

        session.annuler();
        auditTransition(session, avant, session.getStatut());

    }

    private SessionFormation getSessionOrThrow(Integer sessionId) {
        return repository.findById(sessionId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Session not found")
                );
    }

    private void auditTransition(
            SessionFormation session,
            SessionFormationStatut avant,
            SessionFormationStatut apres
    ) {
        String emailAdmin = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        SessionFormationAudit audit = SessionFormationAudit.builder()
                .session(session)
                .statutAvant(avant)
                .statutApres(apres)
                .modifiePar(emailAdmin)
                .dateModification(LocalDateTime.now())
                .build();

        auditRepository.save(audit);
    }

    private SessionResponseDto toDto(SessionFormation session) {
        return new SessionResponseDto(
                session.getIdSession(),
                session.getDateDebut(),
                session.getDateFin(),
                session.getStatut(),
                session.getFormateur() != null
                        ? session.getFormateur().getNom() + " " + session.getFormateur().getPrenom()
                        : null,
                session.getFournisseur() != null
                        ? session.getFournisseur().getNomEntreprise()
                        : null
        );
    }
}
