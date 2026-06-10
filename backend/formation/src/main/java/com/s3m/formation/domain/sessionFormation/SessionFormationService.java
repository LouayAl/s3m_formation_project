package com.s3m.formation.domain.sessionFormation;

import com.s3m.formation.api.dto.ParticipantResponseDto;
import com.s3m.formation.api.dto.SessionFormationResponseDto;
import com.s3m.formation.api.dto.UpdateSessionRequest;
import com.s3m.formation.domain.coutFormation.CoutFormation;
import com.s3m.formation.domain.coutFormation.CoutFormationRepository;
import com.s3m.formation.domain.employe.EmployeRepository;
import com.s3m.formation.domain.entreprise.Entreprise;
import com.s3m.formation.domain.entreprise.EntrepriseRepository;
import com.s3m.formation.domain.formateur.FormateurRepository;
import com.s3m.formation.domain.formation.Formation;
import com.s3m.formation.domain.formation.FormationRepository;
import com.s3m.formation.domain.participation.Participation;
import com.s3m.formation.domain.participation.ParticipationRepository;
import com.s3m.formation.domain.sessionFormation.sessionFormationAudit.SessionFormationAudit;
import com.s3m.formation.domain.sessionFormation.sessionFormationAudit.SessionFormationAuditRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
    private final ParticipationRepository participationRepository;
    private final EmployeRepository employeRepository;
    private final CoutFormationRepository coutFormationRepository;


    /* =========================
       READ
       ========================= */

    public List<SessionFormationResponseDto> getAllSessions() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer entrepriseId = (Integer) auth.getDetails();

        return repository.search(null, null, entrepriseId, null, null)
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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer entrepriseId = (Integer) auth.getDetails();

        return repository.findById(sessionId)
                .filter(s -> s.getEntreprise() != null &&
                        s.getEntreprise().getIdEntreprise().equals(entrepriseId))
                .map(this::toDto)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Session non trouvée"));
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
                .dJours(request.getDJours())
                .statut(SessionFormationStatut.PLANIFIEE)
                .formateur(request.getIdFormateur() != null
                        ? formateurRepository.findById(request.getIdFormateur()).orElse(null)
                        : null)
                .entreprise(entreprise)
                .fournisseur(request.getIdFournisseur() != null
                        ? entrepriseRepository.findById(request.getIdFournisseur()).orElse(null)
                        : null)
                .lieu(request.getLieu())
                .build();

        String ref = generateReference(session);
        session.setReferenceSession(ref);

        SessionFormation saved = repository.save(session);
        seedCoutFormation(saved, formation);
        return saved;
    }

    private void seedCoutFormation(SessionFormation session, Formation formation) {
        if (coutFormationRepository.existsBySession_IdSession(session.getIdSession())) {
            return;
        }

        BigDecimal prixHeure      = formation.getPrixHeureMad();
        BigDecimal prixJour       = formation.getPrixJourMad();
        BigDecimal autresDepenses = formation.getAutresDepenses() != null
                ? formation.getAutresDepenses()
                : BigDecimal.ZERO;

        BigDecimal coutTotal = BigDecimal.ZERO;
        if (prixHeure != null && session.getDHeures() != null) {
            coutTotal = prixHeure.multiply(session.getDHeures());
        } else if (prixJour != null && session.getDJours() != null) {
            coutTotal = prixJour.multiply(session.getDJours());
        }
        coutTotal = coutTotal.add(autresDepenses);

        CoutFormation cout = new CoutFormation();
        cout.setSession(session);
        cout.setRemboursement(formation.getRemboursement());
        cout.setPrixHeureMad(prixHeure);
        cout.setPrixJourMad(prixJour);
        cout.setAutresDepenses(autresDepenses);
        cout.setCoutTotal(coutTotal);

        coutFormationRepository.save(cout);
    }

    /* =========================
       UPDATE
       ========================= */
    public SessionFormationResponseDto updateSession(Integer sessionId, UpdateSessionRequest request) {


        SessionFormation existing = repository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));


        if (request.dHeures() != null) {
            existing.setDHeures(request.dHeures());
            existing.setDJours(request.dJours());
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
        if (request.lieu() != null) existing.setLieu(request.lieu());


        SessionFormation saved = repository.save(existing);

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

        // Map participations to ParticipantResponseDto
        List<ParticipantResponseDto> participants = session.getParticipations() != null
                ? session.getParticipations().stream()
                .map(p -> {
                    var e = p.getEmploye();
                    return new ParticipantResponseDto(
                            e.getIdEmploye(),
                            e.getNom(),
                            e.getPrenom(),
                            e.getEmail(),
                            e.getTelephone(),
                            e.getCin(),
                            e.getMatricule()
                    );
                })
                .toList()
                : List.of();

        int count = participants.size();

        return new SessionFormationResponseDto(
                session.getIdSession(),
                session.getReferenceSession(),
                session.getFormation() != null ? session.getFormation().getIdFormation() : null,
                session.getFormation() != null ? session.getFormation().getModule() : null,
                session.getEntreprise() != null ? session.getEntreprise().getIdEntreprise() : null,  // ✅ add ID
                session.getEntreprise() != null ? session.getEntreprise().getNomEntreprise() : null,
                session.getFournisseur() != null ? session.getFournisseur().getIdEntreprise() : null, // ✅ add ID
                session.getFournisseur() != null ? session.getFournisseur().getNomEntreprise() : null,
                session.getFormateur() != null ? session.getFormateur().getIdFormateur() : null,      // ✅ add ID
                formateurNomComplet,
                session.getDateDebut(),
                session.getDateFin(),
                session.getDHeures(),
                session.getDJours(),
                session.getStatut(),
                count,
                participants,
                session.getLieu()
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

    public void updateParticipants(Integer sessionId, List<Integer> participantIds) {
        SessionFormation session = repository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found"));

        List<Participation> currentParticipations = session.getParticipations();

        // Remove participants not in the new list
        currentParticipations.stream()
                .filter(p -> !participantIds.contains(p.getEmploye().getIdEmploye()))
                .forEach(participationRepository::delete);

        // Add new participants that are not already added
        participantIds.forEach(id -> {
            boolean alreadyExists = currentParticipations.stream()
                    .anyMatch(p -> p.getEmploye().getIdEmploye().equals(id));
            if (!alreadyExists) {
                participationRepository.save(
                        new Participation(session, employeRepository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException("Employe not found")))
                );
            }
        });
    }

    private boolean currentUserIsAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;

        // Check if any granted authority equals "ADMIN" (exact match with DB role)
        for (GrantedAuthority authority : auth.getAuthorities()) {
            if ("ADMIN".equals(authority.getAuthority()) || "EQUIPMENT_MANAGER".equals(authority.getAuthority())) {
                return true;
            }
        }
        return false;
    }

}
