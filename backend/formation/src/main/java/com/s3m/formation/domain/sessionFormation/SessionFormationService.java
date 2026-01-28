package com.s3m.formation.domain.sessionFormation;

import com.s3m.formation.api.dto.SessionFormationResponseDto;
import com.s3m.formation.api.dto.UpdateSessionRequest;
import com.s3m.formation.domain.entreprise.Entreprise;
import com.s3m.formation.domain.entreprise.EntrepriseRepository;
import com.s3m.formation.domain.formateur.FormateurRepository;
import com.s3m.formation.domain.formation.Formation;
import com.s3m.formation.domain.formation.FormationRepository;
import com.s3m.formation.domain.sessionFormation.sessionFormationAudit.SessionFormationAudit;
import com.s3m.formation.domain.sessionFormation.sessionFormationAudit.SessionFormationAuditRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(SessionFormationService.class);

    private final SessionFormationRepository repository;
    private final SessionFormationAuditRepository auditRepository;
    private final FormationRepository formationRepository;
    private final FormateurRepository formateurRepository;
    private final EntrepriseRepository entrepriseRepository;

    /* =========================
       READ
       ========================= */

    public List<SessionFormationResponseDto> getAllSessions() {
        return repository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<SessionFormationResponseDto> getSessionsByFormation(Integer formationId) {
        return repository.findByFormation_IdFormation(formationId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public SessionFormationResponseDto getSession(Integer sessionId) {
        return repository.findById(sessionId)
                .map(this::toDto)
                .orElse(null);
    }

    /* =========================
       CREATE
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
       UPDATE
       ========================= */
    public SessionFormationResponseDto updateSession(Integer sessionId, UpdateSessionRequest request) {

        log.info("Update request received: {}", request);

        SessionFormation existing = repository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        log.info("Existing session before update: dHeures={}, dJours={}", existing.getDHeures(), existing.getDJours());

        if (request.dHeures() != null) {
            existing.setDHeures(request.dHeures());
            existing.setDJours(request.dHeures().divide(BigDecimal.valueOf(8), 2, RoundingMode.HALF_UP));
        }
        if (request.dateDebut() != null) existing.setDateDebut(request.dateDebut());
        if (request.dateFin() != null) existing.setDateFin(request.dateFin());
        if (request.idFormateur() != null)
            existing.setFormateur(formateurRepository.findById(request.idFormateur()).orElse(null));
        if (request.idEntreprise() != null)
            existing.setEntreprise(entrepriseRepository.findById(request.idEntreprise()).orElse(null));
        if (request.idFournisseur() != null)
            existing.setFournisseur(entrepriseRepository.findById(request.idFournisseur()).orElse(null));
        if (request.idFormation() != null)
            existing.setFormation(formationRepository.findById(request.idFormation()).orElse(null));
        if (request.statut() != null) existing.setStatut(request.statut());

        log.info("Existing session after update: dHeures={}, dJours={}", existing.getDHeures(), existing.getDJours());

        SessionFormation saved = repository.save(existing);
        log.info("Session saved: {}", saved.getIdSession());

        return toDto(saved);
    }


    /* =========================
       DELETE
       ========================= */

    public void deleteSession(Integer sessionId) {
        SessionFormation session = repository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found"));
        repository.delete(session);
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

    /* =========================
       DTO MAPPING
       ========================= */

    public SessionFormationResponseDto toDto(SessionFormation session) {
        String formateurNomComplet = session.getFormateur() != null
                ? session.getFormateur().getNom() + " " + session.getFormateur().getPrenom()
                : null;

        return new SessionFormationResponseDto(
                session.getIdSession(),
                session.getReferenceSession(),
                session.getFormation().getIdFormation(),
                session.getFormation().getModule(),
                session.getEntreprise() != null ? session.getEntreprise().getNomEntreprise() : null,
                session.getFournisseur() != null ? session.getFournisseur().getNomEntreprise() : null,
                formateurNomComplet,
                session.getDateDebut(),
                session.getDateFin(),
                session.getDHeures(),
                session.getDJours(),
                session.getStatut()
        );
    }

    /* =========================
       REFERENCE GENERATION
       ========================= */

    private String generateReference(SessionFormation session) {
        if (session.getFormation() == null || session.getFormation().getModule() == null) {
            throw new IllegalStateException("Impossible de générer la référence : formation manquante");
        }

        String moduleCode = session.getFormation().getModule()
                .substring(0, Math.min(3, session.getFormation().getModule().length()))
                .toUpperCase();

        String reference;
        do {
            int randomNumber = (int) (Math.random() * 9000) + 1000;
            reference = String.format("%s-%d", moduleCode, randomNumber);
        } while (repository.existsByReferenceSession(reference));
        return reference;
    }
}
