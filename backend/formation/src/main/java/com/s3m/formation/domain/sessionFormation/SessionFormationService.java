package com.s3m.formation.domain.sessionFormation;

import com.s3m.formation.api.dto.SessionResponseDto;
import com.s3m.formation.domain.entreprise.Entreprise;
import com.s3m.formation.domain.entreprise.EntrepriseRepository;
import com.s3m.formation.domain.formateur.FormateurRepository;
import com.s3m.formation.domain.formation.Formation;
import com.s3m.formation.domain.formation.FormationRepository;
import com.s3m.formation.domain.sessionFormation.sessionFormationAudit.SessionFormationAudit;
import com.s3m.formation.domain.sessionFormation.sessionFormationAudit.SessionFormationAuditRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SessionFormationService {

    private final SessionFormationRepository repository;
    private final SessionFormationAuditRepository auditRepository;
    private final FormationRepository formationRepository;
    private final FormateurRepository formateurRepository;
    private final EntrepriseRepository entrepriseRepository;

    /* =========================
       READ
       ========================= */

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
                .orElseThrow(() -> new EntityNotFoundException("Session not found"));
    }

    private void auditTransition(SessionFormation session,
                                 SessionFormationStatut avant,
                                 SessionFormationStatut apres) {

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

    SessionResponseDto toDto(SessionFormation session) {
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

    /* =========================
       CREATE SESSION (SAFE)
       ========================= */

    public SessionFormation createSession(CreateSessionRequest request) {
        try {
            return createSessionInternal(request);
        } catch (DataIntegrityViolationException e) {
            // Retry once if reference collision happens
            return createSessionInternal(request);
        }
    }

    private SessionFormation createSessionInternal(CreateSessionRequest request) {

        Formation formation = formationRepository.findById(request.getIdFormation())
                .orElseThrow(() -> new RuntimeException("Formation not found"));

        Entreprise entreprise = entrepriseRepository.findById(request.getIdEntreprise())
                .orElseThrow(() -> new RuntimeException("Entreprise not found"));

        SessionFormation session = SessionFormation.builder()
                .formation(formation)
                .dateDebut(request.getDateDebut())
                .dateFin(request.getDateFin())
                .dHeures(request.getDHeures())
                .dJours(request.getDHeures().divide(BigDecimal.valueOf(8), 2, RoundingMode.HALF_UP))
                .statut(SessionFormationStatut.PLANIFIEE)
                .formateur(request.getIdFormateur() != null
                        ? formateurRepository.findById(request.getIdFormateur()).orElse(null)
                        : null)
                .entreprise(entreprise)
                .fournisseur(request.getIdFournisseur() != null
                        ? entrepriseRepository.findById(request.getIdFournisseur()).orElse(null)
                        : null)
                .build();

        String ref = generateReference(session);
        session.setReferenceSession(ref);

        return repository.save(session);
    }

    /* =========================
       REFERENCE GENERATION
       ========================= */

    private String generateReference(SessionFormation session) {

        if (session.getFormation() == null || session.getDateDebut() == null || session.getDHeures() == null) {
            throw new IllegalStateException("Impossible de générer la référence : données incomplètes");
        }

        String module = session.getFormation().getModule();
        String moduleCode = module.substring(0, Math.min(3, module.length()))
                .toUpperCase();

        int heures = session.getDHeures().intValue();
        int year = session.getDateDebut().getYear();

        long count = repository.countByModuleAndYearAndHeures(
                session.getFormation().getIdFormation(),
                year,
                session.getDHeures()
        );

        return String.format(
                "%s-%dH-%d-%03d",
                moduleCode,
                heures,
                year,
                count + 1
        );
    }
}
