package com.s3m.formation.domain.sessionFormation;

import com.s3m.formation.api.dto.ParticipantResponseDto;
import com.s3m.formation.api.dto.SessionFormationResponseDto;
import com.s3m.formation.api.dto.UpdateSessionRequest;
import com.s3m.formation.domain.coutFormation.CoutFormation;
import com.s3m.formation.domain.coutFormation.CoutFormationRepository;
import com.s3m.formation.domain.employe.EmployeRepository;
import com.s3m.formation.domain.employe.Employe;
import com.s3m.formation.domain.entreprise.Entreprise;
import com.s3m.formation.domain.entreprise.EntrepriseRepository;
import com.s3m.formation.domain.formateur.FormateurRepository;
import com.s3m.formation.domain.formation.Formation;
import com.s3m.formation.domain.formation.FormationRepository;
import com.s3m.formation.domain.participation.Participation;
import com.s3m.formation.domain.participation.ParticipationRepository;
import com.s3m.formation.domain.sessionFormation.sessionFormationAudit.SessionFormationAudit;
import com.s3m.formation.domain.sessionFormation.sessionFormationAudit.SessionFormationAuditRepository;
import com.s3m.formation.security.util.AuthDetails;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import java.util.Comparator;
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
    private final EmailNotificationService emailNotificationService;


    /* =========================
       READ
       ========================= */

    public List<SessionFormationResponseDto> getAllSessions(Integer requestedEntrepriseId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer entrepriseId = currentUserCanViewAllEntreprises()
                ? requestedEntrepriseId
                : (auth.getDetails() instanceof AuthDetails d ? d.getEntrepriseId() : null);

        return repository.search(null, null, entrepriseId, null, null, false)
                .stream()
                .filter(this::visibleToCurrentDepartmentChef)
                .map(this::toDto)
                .toList();
    }

    /* =========================
       READ — PAGINATED (server-side)
       ========================= */

    public Page<SessionFormationResponseDto> getSessionsPaginated(
            Integer requestedEntrepriseId,
            String search,
            List<Integer> years,
            List<SessionFormationStatut> statuts,
            Boolean facture,
            int page,
            int size,
            String sortBy,
            String sortDir,
            String filterField,   // ← add
            String filterValue    // ← add
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer entrepriseId = currentUserCanViewAllEntreprises()
                ? requestedEntrepriseId
                : (auth.getDetails() instanceof AuthDetails d ? d.getEntrepriseId() : null);

        Boolean effectiveFacture = currentUserIsFinance() ? facture : null;

        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir)
                ? Sort.Direction.DESC : Sort.Direction.ASC;

        String mappedField = switch (sortBy) {
            case "formation"           -> "formation.module";
            case "entrepriseNom"       -> "entreprise.nomEntreprise";
            case "fournisseurNom"      -> "fournisseur.nomEntreprise";
            case "formateurNomComplet" -> "formateur.nom";
            case "referenceSession"    -> "referenceSession";
            case "dateDebut"           -> "dateDebut";
            case "dateFin"             -> "dateFin";
            case "dHeures"             -> "dHeures";
            case "dJours"              -> "dJours";
            case "lieu"                -> "lieu";
            case "statut"              -> "statut";
            default -> "idSession";
        };

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, mappedField));

        List<Integer> yearsFilter  = (years == null || years.isEmpty()) ? null : years;
        List<SessionFormationStatut> statutsFilter = (statuts == null || statuts.isEmpty()) ? null : statuts;

        // Normalize column filter — both must be present or neither applies
        String colField  = (filterField != null && !filterField.isBlank()) ? filterField : null;
        String colFilter = (filterValue != null && !filterValue.isBlank()) ? filterValue : null;
        if (colField == null || colFilter == null) { colField = null; colFilter = null; }

        return repository.findPaginated(
                entrepriseId, currentUserIsDepartmentChef() ? requireCurrentDepartment() : null,
                search, yearsFilter, statutsFilter,
                effectiveFacture, currentUserIsFinance(), colField, colFilter, pageable
        ).map(this::toDto);
    }

    // Distinct years available for the year-filter dropdown (scoped the same way as sessions).
    public List<Integer> getAvailableYears(Integer requestedEntrepriseId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer entrepriseId = currentUserCanViewAllEntreprises()
                ? requestedEntrepriseId
                : (auth.getDetails() instanceof AuthDetails d ? d.getEntrepriseId() : null);

        return repository.findAllDateDebuts(entrepriseId, currentUserIsFinance()).stream()
                .map(LocalDate::getYear)
                .distinct()
                .sorted(Comparator.reverseOrder())
                .toList();
    }

    public List<SessionFormationResponseDto> getSessionsByFormation(Integer formationId) {
        return repository.findByFormation_IdFormation(formationId)
                .stream()
                .filter(s -> !s.isCreatedInEm())
                .filter(this::visibleToCurrentDepartmentChef)
                .map(this::toDto)
                .toList();
    }

    public SessionFormationResponseDto getSession(Integer sessionId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer entrepriseId = auth.getDetails() instanceof AuthDetails d ? d.getEntrepriseId() : null;

        // ADMIN_FINANCE (like ADMIN) isn't scoped to a single entreprise, so the
        // ownership check below only applies to entreprise-scoped roles.
        if (currentUserCanViewAllEntreprises()) {
            return repository.findById(sessionId)
                    .filter(s -> !s.isCreatedInEm())
                    .map(this::toDto)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Session non trouvée"));
        }

        return repository.findById(sessionId)
                .filter(s -> !s.isCreatedInEm())
                .filter(s -> s.getEntreprise() != null &&
                        s.getEntreprise().getIdEntreprise().equals(entrepriseId))
                .filter(this::visibleToCurrentDepartmentChef)
                .map(this::toDto)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Session non trouvée"));
    }

    /* =========================
       CREATE
       ========================= */

    public SessionFormation createSession(CreateSessionRequest request) {
        return createSession(request, false);
    }

    public SessionFormation createEmSession(CreateSessionRequest request) {
        return createSession(request, true);
    }

    private SessionFormation createSession(CreateSessionRequest request, boolean createdInEm) {
        try {
            return createSessionInternal(request, createdInEm);
        } catch (DataIntegrityViolationException e) {
            // Retry once if reference collision happens
            return createSessionInternal(request, createdInEm);
        }
    }

    private SessionFormation createSessionInternal(CreateSessionRequest request, boolean createdInEm) {
        Formation formation = formationRepository.findById(request.getIdFormation())
                .orElseThrow(() -> new RuntimeException("Formation not found"));

        Entreprise entreprise = entrepriseRepository.findById(request.getIdEntreprise())
                .orElseThrow(() -> new RuntimeException("Entreprise not found"));

        assertFormationMatchesEntreprise(formation, entreprise.getIdEntreprise());

        SessionFormation session = SessionFormation.builder()
                .formation(formation)
                .dateDebut(request.getDateDebut())
                .dateFin(request.getDateFin())
                .dHeures(positiveOr(request.getDHeures(), formation.getDureeHeures()))
                .dJours(positiveOr(request.getDJours(), formation.getDureeJours()))
                .statut(SessionFormationStatut.PLANIFIEE)
                .formateur(request.getIdFormateur() != null
                        ? formateurRepository.findById(request.getIdFormateur()).orElse(null)
                        : null)
                .entreprise(entreprise)
                .fournisseur(request.getIdFournisseur() != null
                        ? entrepriseRepository.findById(request.getIdFournisseur()).orElse(null)
                        : null)
                .lieu(request.getLieu())
                .createdInEm(createdInEm)
                .build();
        syncSupplierTrainerConfirmation(session, true);
        List<LocalDate> jours = (request.getJours() != null && !request.getJours().isEmpty())
                ? request.getJours()
                : expandRange(request.getDateDebut(), request.getDateFin());
        applyJours(session, jours);

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
        Integer previousFormateurId = existing.getFormateur() != null ? existing.getFormateur().getIdFormateur() : null;
        Integer previousSupplierId = existing.getFournisseur() != null ? existing.getFournisseur().getIdEntreprise() : null;

        if (request.referenceSession() != null && !request.referenceSession().isBlank()) {
            // Check uniqueness — reject if another session already has this ref
            if (repository.existsByReferenceSessionAndIdSessionNot(
                    request.referenceSession(), sessionId)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Cette référence est déjà utilisée par une autre session.");
            }
            existing.setReferenceSession(request.referenceSession());
        }
        if (request.dHeures() != null) existing.setDHeures(request.dHeures());
        if (request.dJours() != null) existing.setDJours(request.dJours());
        if (request.jours() != null && !request.jours().isEmpty()) {
            applyJours(existing, request.jours());
        } else if (request.dateDebut() != null || request.dateFin() != null) {
            // legacy callers that only send a range
            if (request.dateDebut() != null) existing.setDateDebut(request.dateDebut());
            if (request.dateFin() != null)   existing.setDateFin(request.dateFin());
            applyJours(existing, expandRange(existing.getDateDebut(), existing.getDateFin()));
        }
        if (request.idFormateur() != null)
            existing.setFormateur(formateurRepository.findById(request.idFormateur()).orElse(null));
        if (request.idEntreprise() != null)
            existing.setEntreprise(entrepriseRepository.findById(request.idEntreprise()).orElse(null));
        if (request.idFournisseur() != null)
            existing.setFournisseur(entrepriseRepository.findById(request.idFournisseur()).orElse(null));
        if (request.idFormation() != null)
            existing.setFormation(formationRepository.findById(request.idFormation()).orElse(null));
        if (existing.getFormation() != null) {
            existing.setDHeures(positiveOr(existing.getDHeures(), existing.getFormation().getDureeHeures()));
            existing.setDJours(positiveOr(existing.getDJours(), existing.getFormation().getDureeJours()));
        }
        if (request.statut() != null) existing.setStatut(request.statut());
        if (request.lieu() != null)   existing.setLieu(request.lieu());

        Integer currentFormateurId = existing.getFormateur() != null ? existing.getFormateur().getIdFormateur() : null;
        Integer currentSupplierId = existing.getFournisseur() != null ? existing.getFournisseur().getIdEntreprise() : null;
        boolean trainerOrSupplierChanged = !java.util.Objects.equals(previousFormateurId, currentFormateurId)
                || !java.util.Objects.equals(previousSupplierId, currentSupplierId);
        syncSupplierTrainerConfirmation(existing, trainerOrSupplierChanged);

        // Re-validate consistency whenever either side could have changed (or even if
        // neither did — cheap guard against any pre-existing inconsistent data).
        if (existing.getFormation() != null && existing.getEntreprise() != null) {
            assertFormationMatchesEntreprise(existing.getFormation(), existing.getEntreprise().getIdEntreprise());
        }

        return toDto(repository.save(existing));
    }


    /* =========================
       DELETE
       ========================= */

    public void deleteSession(Integer sessionId) {
        SessionFormation session = repository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found"));

        // Delete dependent CoutFormation rows first to avoid FK violation
        List<CoutFormation> couts = coutFormationRepository.findBySession_IdSession(sessionId);
        coutFormationRepository.deleteAll(couts);

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

    /* =========================
       FACTURATION (ADMIN_FINANCE only — enforced at the controller via
       @PreAuthorize, this method assumes the caller already has the right)
       ========================= */

    public SessionFormationResponseDto toggleFacture(Integer sessionId) {
        SessionFormation session = getSessionOrThrow(sessionId);
        boolean current = Boolean.TRUE.equals(session.getSessionFacturee());
        session.setSessionFacturee(!current);
        SessionFormation saved = repository.save(session);
        log.info("Session {} (id={}) marquée comme {} par {}",
                saved.getReferenceSession(), saved.getIdSession(),
                saved.getSessionFacturee() ? "facturée" : "non facturée",
                SecurityContextHolder.getContext().getAuthentication().getName());
        return toDto(saved);
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
   FORMATEUR CONFIRMATION
   ========================= */

    public SessionFormationResponseDto notifyFormateur(Integer sessionId) {
        SessionFormation session = getSessionOrThrow(sessionId);

        if (isSupplierOutsideS3m(session) && session.getFormateur() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "La confirmation du formateur est automatique pour ce fournisseur.");
        }

        if (session.getNotificationEnvoyeeLe() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Une notification a déjà été envoyée à ce formateur. " +
                            "Veuillez le contacter directement pour obtenir sa confirmation.");
        }

        var formateur = session.getFormateur();
        if (formateur == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Aucun formateur n'est assigné à cette session.");
        }
        if (formateur.getEmail() == null || formateur.getEmail().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Ce formateur n'a pas d'adresse email enregistrée.");
        }

        emailNotificationService.sendFormateurConfirmationRequest(session, formateur);

        session.setNotificationEnvoyeeLe(LocalDateTime.now());
        return toDto(repository.save(session));
    }

    public SessionFormationResponseDto confirmFormateur(Integer sessionId) {
        SessionFormation session = getSessionOrThrow(sessionId);

        String adminEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        session.setFormateurConfirme(true);
        session.setFormateurConfirmeLe(LocalDateTime.now());
        session.setFormateurConfirmePar(adminEmail);

        return toDto(repository.save(session));
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
                .filter(p -> !currentUserIsDepartmentChef() || (p.getEmploye().getDepartement() != null
                        && requireCurrentDepartment().equals(p.getEmploye().getDepartement().getId())))
                .map(p -> {
                    var e = p.getEmploye();
                    return new ParticipantResponseDto(
                            e.getIdEmploye(),
                            e.getNom(),
                            e.getPrenom(),
                            e.getEmail(),
                            e.getTelephone(),
                            e.getCin(),
                            e.getMatricule(),
                            e.getDepartement() != null ? e.getDepartement().getNom() : null
                    );
                })
                .toList()
                : List.of();

        int count = participants.size();

        // The invoice state is visible to all users for row-color context;
        // only ADMIN_FINANCE can change it (enforced by the controller).
        Boolean factureForResponse = session.getSessionFacturee();
        Boolean formateurInterne = session.getFormateur() != null
                && session.getFormateur().getEntreprise() != null
                && session.getEntreprise() != null
                && session.getFormateur().getEntreprise().getIdEntreprise()
                    .equals(session.getEntreprise().getIdEntreprise());

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
                session.getLieu(),
                factureForResponse,
                isSupplierOutsideS3m(session) && session.getFormateur() != null
                        ? true : session.getFormateurConfirme(),
                session.getNotificationEnvoyeeLe(),
                session.getFormateurConfirmeLe(),
                session.resolveJours(),
                formateurInterne
        );
    }

    private boolean isSupplierOutsideS3m(SessionFormation session) {
        return session.getFournisseur() != null
                && session.getFournisseur().getNomEntreprise() != null
                && !"S3M".equalsIgnoreCase(session.getFournisseur().getNomEntreprise().trim());
    }

    private void syncSupplierTrainerConfirmation(SessionFormation session, boolean assignmentChanged) {
        if (isSupplierOutsideS3m(session) && session.getFormateur() != null) {
            if (!Boolean.TRUE.equals(session.getFormateurConfirme())) {
                session.setFormateurConfirme(true);
                session.setFormateurConfirmeLe(LocalDateTime.now());
                session.setFormateurConfirmePar("SYSTEM");
            }
            return;
        }

        if (assignmentChanged) {
            session.setFormateurConfirme(false);
            session.setFormateurConfirmeLe(null);
            session.setFormateurConfirmePar(null);
            session.setNotificationEnvoyeeLe(null);
        }
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
        if (currentUserIsDepartmentChef()) {
            Integer departmentId = requireCurrentDepartment();
            if (!visibleToCurrentDepartmentChef(session)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Session non trouvée pour ce département.");
            }
            java.util.Set<Integer> currentIds = currentParticipations.stream()
                    .map(p -> p.getEmploye().getIdEmploye()).collect(java.util.stream.Collectors.toSet());
            if (!participantIds.containsAll(currentIds)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Un chef de département peut ajouter des participants, pas en retirer.");
            }
            for (Integer participantId : participantIds) {
                Employe candidate = employeRepository.findById(participantId)
                        .orElseThrow(() -> new EntityNotFoundException("Employe not found"));
                if (!currentIds.contains(participantId)
                        && (candidate.getDepartement() == null
                        || !departmentId.equals(candidate.getDepartement().getId())
                        || session.getEntreprise() == null
                        || !session.getEntreprise().getIdEntreprise().equals(candidate.getEntreprise().getIdEntreprise()))) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Vous pouvez uniquement ajouter des employés de votre département.");
                }
            }
        }

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

    /**
     * Guards against a formation and a session being assigned to different entreprises.
     * Formations can share the same module/name across entreprises, so this is a hard
     * server-side check, not just a frontend warning — the person can't get around it
     * by hitting the API directly.
     */
    private void assertFormationMatchesEntreprise(Formation formation, Integer sessionEntrepriseId) {
        Integer formationEntrepriseId = formation.getEntreprise() != null
                ? formation.getEntreprise().getIdEntreprise()
                : null;

        if (formationEntrepriseId == null || !formationEntrepriseId.equals(sessionEntrepriseId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "La formation sélectionnée appartient à une autre entreprise que celle choisie pour la session. " +
                            "Veuillez changer la formation ou l'entreprise de la session.");
        }
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

    // Renamed from currentUserIsAdminOnly(): ADMIN_FINANCE also needs to browse
    // and filter sessions across every entreprise (invoicing isn't scoped to
    // one company), so it shares this "sees everything" gate with ADMIN —
    // without gaining any of ADMIN's write permissions, which stay enforced
    // separately at the controller level via @PreAuthorize.
    private boolean currentUserCanViewAllEntreprises() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;

        for (GrantedAuthority authority : auth.getAuthorities()) {
            String a = authority.getAuthority();
            if ("ADMIN".equals(a) || "ADMIN_FINANCE".equals(a)) {
                return true;
            }
        }
        return false;
    }

    // Gate for the facture field specifically — ADMIN can view all sessions
    // via currentUserCanViewAllEntreprises() above, but per the agreed rules
    // ADMIN must NOT see or toggle facturation, so this is a separate check.
    private boolean currentUserIsFinance() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;

        for (GrantedAuthority authority : auth.getAuthorities()) {
            if ("ADMIN_FINANCE".equals(authority.getAuthority())) {
                return true;
            }
        }
        return false;
    }

    private boolean currentUserIsDepartmentChef() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> "CHEF_DEPARTEMENT".equals(a.getAuthority()));
    }

    private Integer requireCurrentDepartment() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer id = auth != null && auth.getDetails() instanceof AuthDetails details
                ? details.getDepartementId() : null;
        if (id == null) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Aucun département n'est associé à ce compte.");
        return id;
    }

    private boolean visibleToCurrentDepartmentChef(SessionFormation session) {
        if (!currentUserIsDepartmentChef()) return true;
        Integer departmentId = requireCurrentDepartment();
        return session.getParticipations() != null && session.getParticipations().stream()
                .anyMatch(p -> p.getEmploye() != null && p.getEmploye().getDepartement() != null
                        && departmentId.equals(p.getEmploye().getDepartement().getId()));
    }

    private List<LocalDate> expandRange(LocalDate debut, LocalDate fin) {
        if (debut == null || fin == null || fin.isBefore(debut)) return List.of();
        return debut.datesUntil(fin.plusDays(1)).toList();
    }

    private BigDecimal positiveOr(BigDecimal value, BigDecimal fallback) {
        return value != null && value.compareTo(BigDecimal.ZERO) > 0 ? value : fallback;
    }

    // Single source of truth: dateDebut/dateFin always mirror the first/last selected day.
    private void applyJours(SessionFormation session, java.util.Collection<LocalDate> jours) {
        java.util.TreeSet<LocalDate> sorted = new java.util.TreeSet<>(jours);
        if (sorted.isEmpty()) return;
        session.getJoursSession().clear();
        session.getJoursSession().addAll(sorted);
        session.setDateDebut(sorted.first());
        session.setDateFin(sorted.last());
    }

}
