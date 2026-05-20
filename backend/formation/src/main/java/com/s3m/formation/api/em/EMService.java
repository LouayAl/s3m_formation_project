package com.s3m.formation.api.em;

import com.s3m.formation.api.dto.*;
import com.s3m.formation.domain.employe.Employe;
import com.s3m.formation.domain.employe.EmployeRepository;
import com.s3m.formation.domain.evaluation.Evaluation;
import com.s3m.formation.domain.evaluation.EvaluationCritere;
import com.s3m.formation.domain.evaluation.EvaluationCritereRepository;
import com.s3m.formation.domain.evaluation.EvaluationRepository;
import com.s3m.formation.domain.formation.FormationRepository;
import com.s3m.formation.domain.sessionFormation.SessionFormation;
import com.s3m.formation.domain.sessionFormation.SessionFormationRepository;
import com.s3m.formation.domain.sessionFormation.SessionFormationStatut;
import com.s3m.formation.security.util.SecurityContextUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.s3m.formation.domain.sessionCritere.SessionCritere;
import com.s3m.formation.domain.sessionCritere.SessionCritereRepository;
import com.s3m.formation.domain.sessionProgramme.SessionDailyProgram;
import com.s3m.formation.domain.sessionProgramme.SessionDailyProgramEntry;
import com.s3m.formation.domain.sessionProgramme.SessionDailyProgramRepository;
import com.s3m.formation.domain.participation.ParticipationRepository;
import com.s3m.formation.api.dto.EmployeResponseDto;
import com.s3m.formation.domain.formateur.Formateur;
import com.s3m.formation.domain.formateur.FormateurRepository;
import java.util.Optional;

import java.util.*;
import java.util.stream.Collectors;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EMService {

    private final EvaluationRepository    evalRepo;
    private final SessionFormationRepository sessionRepo;
    private final EmployeRepository       employeRepo;
    private final SessionCritereRepository critereRepo;
    private final ParticipationRepository  participationRepo;
    private final FormateurRepository formateurRepo;
    private final EvaluationCritereRepository evalCritereRepo;
    private final SessionDailyProgramRepository dailyProgramRepo;
    private final FormationRepository formationRepo;

    // ─── Dashboard KPIs ──────────────────────────────────────────────────────
    public EMDashboardKpiDto getDashboardKpis() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer entrepriseId = (Integer) auth.getDetails();

        List<SessionFormation> allSessions = sessionRepo.search(
                null, null, entrepriseId, null, null
        );

        long enCours    = allSessions.stream().filter(s -> SessionFormationStatut.EN_COURS.equals(s.getStatut())).count();
        long terminees  = allSessions.stream().filter(s -> SessionFormationStatut.TERMINEE.equals(s.getStatut())).count();
        long planifiees = allSessions.stream().filter(s -> SessionFormationStatut.PLANIFIEE.equals(s.getStatut())).count();

        // Distinct participants enrolled across all sessions
        long totalParticipants = allSessions.stream()
                .flatMap(s -> s.getParticipations().stream())
                .map(p -> p.getEmploye() != null ? p.getEmploye().getIdEmploye() : null)
                .filter(Objects::nonNull)
                .distinct()
                .count();

        // Participants in EN_COURS sessions
        long participantsEnDirect = allSessions.stream()
                .filter(s -> SessionFormationStatut.EN_COURS.equals(s.getStatut()))
                .flatMap(s -> s.getParticipations().stream())
                .map(p -> p.getEmploye() != null ? p.getEmploye().getIdEmploye() : null)
                .filter(Objects::nonNull)
                .distinct()
                .count();

        long totalEmployes      = employeRepo.count();
        long evaluationsSaisies = evalRepo.countTotalEvaluations();

        return new EMDashboardKpiDto(
                enCours, terminees, planifiees,
                totalParticipants, participantsEnDirect,
                totalEmployes, evaluationsSaisies
        );
    }

    // ─── Evaluations for a session ────────────────────────────────────────────
    public List<EvaluationDto> getEvaluationsForSession(Integer sessionId) {
        return evalRepo.findBySession_IdSessionOrderByJourAsc(sessionId)
                .stream().map(this::toDto).toList();
    }

    // ─── Evaluations for a participant in a session ───────────────────────────
    public List<EvaluationDto> getEvaluationsForParticipant(Integer sessionId, Integer employeId) {
        return evalRepo.findBySession_IdSessionAndEmploye_IdEmployeOrderByJourAsc(sessionId, employeId)
                .stream().map(this::toDto).toList();
    }

    // ─── Per-participant stats for a session ──────────────────────────────────
    public List<Map<String, Object>> getParticipantStats(Integer sessionId) {
        return evalRepo.getParticipantStatsForSession(sessionId).stream().map(row -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("idEmploye",    row[0]);
            m.put("avgScore",     row[1]);
            m.put("joursEvalues", row[2]);
            m.put("absences",     row[3]);
            return m;
        }).toList();
    }

    // ─── Save (create or update) an evaluation — upsert ──────────────────────
    @Transactional
    public EvaluationDto saveEvaluation(EvaluationRequest req) {

        if (req.idSession() == null || req.idEmploye() == null || req.jour() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "idSession, idEmploye and jour are required");
        }


        SessionFormation session = sessionRepo.findById(req.idSession())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Session non trouvée"));

        Employe employe = employeRepo.findById(req.idEmploye())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employé non trouvé"));

        boolean isParticipant = participationRepo
                .existsBySession_IdSessionAndEmploye_IdEmploye(
                        req.idSession(), req.idEmploye()
                );

        if (!isParticipant) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cet employé n'est pas participant à cette session");
        }

        // If trainer, verify session is assigned to them
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isTrainer = auth.getAuthorities().stream()
                .anyMatch(a -> "TRAINER".equals(a.getAuthority()));

        if (isTrainer) {
            String email = (String) auth.getPrincipal();
            Formateur formateur = formateurRepo.findByEmail(email)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.FORBIDDEN, "Formateur non trouvé"));
            if (session.getFormateur() == null ||
                    !session.getFormateur().getIdFormateur().equals(formateur.getIdFormateur())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Vous ne pouvez évaluer que vos sessions assignées");
            }
        }

        // Upsert: update if exists, create if not
        Evaluation eval = evalRepo
                .findBySession_IdSessionAndEmploye_IdEmployeAndJour(
                        req.idSession(), req.idEmploye(), req.jour())
                .orElseGet(() -> Evaluation.builder()
                        .session(session)
                        .employe(employe)
                        .jour(req.jour())
                        .build());

        eval.setPresence(req.presence() != null ? req.presence() : "PRESENT");
        eval.setRemarques(req.remarques());
        eval.setDureeHeures(req.dureeHeures());

        // Replace all criteria scores
        eval.getCriteres().clear();
        evalRepo.saveAndFlush(eval);
        if (req.scores() != null) {
            req.scores().forEach((idx, score) -> {
                if (score != null && score >= 1 && score <= 4) {
                    eval.getCriteres().add(EvaluationCritere.builder()
                            .evaluation(eval)
                            .critereIndex(idx)
                            .score(score)
                            .build());
                }
            });
        }

        return toDto(evalRepo.save(eval));
    }

    // ─── Get criteria for a session day ──────────────────────────────────────────
    public List<SessionCritereDto> getCriteres(Integer sessionId, Integer jour) {
        return critereRepo
                .findBySession_IdSessionAndJourOrderByCritereIndexAsc(sessionId, jour)
                .stream()
                .map(c -> new SessionCritereDto(
                        c.getId(),
                        c.getJour(),
                        c.getCritereIndex(),
                        c.getLibelle(),
                        c.getCategorie()
                ))
                .toList();
    }

    // ─── Save (replace) criteria for a session day ────────────────────────────────
    @Transactional
    public List<SessionCritereDto> saveCriteres(
            Integer sessionId, Integer jour, SessionCritereRequest req) {

        SessionFormation session = sessionRepo.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Session non trouvée"));

        int totalDays = session.getDJours() != null
                ? Math.max(1, session.getDJours().setScale(0, RoundingMode.CEILING).intValue())
                : Math.max(1, jour);

        // Filter out blank criteres (categories with no libelle are skipped)
        List<SessionCritereRequest.CritereEntry> entries = req.criteres().stream()
                .filter(e -> e.libelle() != null && !e.libelle().trim().isBlank())
                .map(e -> new SessionCritereRequest.CritereEntry(
                        e.libelle().trim(),
                        e.categorie() != null && !e.categorie().trim().isBlank()
                                ? e.categorie().trim()
                                : null
                ))
                .toList();

        if (entries.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Veuillez ajouter au moins un critere.");
        }

        List<String> libelles = entries.stream()
                .map(SessionCritereRequest.CritereEntry::libelle)
                .toList();

        realignEvaluationScores(sessionId, jour, libelles);

        evalCritereRepo.deleteByEvaluation_Session_IdSessionAndCritereIndexGreaterThanEqual(
                sessionId,
                libelles.size()
        );
        evalCritereRepo.flush();

        // One criteria set applies to every day of the session.
        critereRepo.deleteBySession_IdSession(sessionId);
        critereRepo.flush();

        List<SessionCritere> saved = new ArrayList<>();
        for (int day = 1; day <= totalDays; day++) {
            for (int i = 0; i < entries.size(); i++) {
                SessionCritereRequest.CritereEntry entry = entries.get(i);
                saved.add(critereRepo.save(
                        SessionCritere.builder()
                                .session(session)
                                .jour(day)
                                .critereIndex(i)
                                .libelle(entry.libelle())
                                .categorie(entry.categorie())
                                .build()
                ));
            }
        }

        return saved.stream()
                .filter(c -> c.getJour().equals(jour))
                .map(c -> new SessionCritereDto(
                        c.getId(),
                        c.getJour(),
                        c.getCritereIndex(),
                        c.getLibelle(),
                        c.getCategorie()
                ))
                .toList();
    }


    // ─── Sessions filtered by current user's entreprise ───────────────────────
    public List<SessionFormationResponseDto> getSessionsForCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer entrepriseId = (Integer) auth.getDetails();

        return sessionRepo.search(null, null, entrepriseId, null, null)
                .stream()
                .map(this::toSessionDto)
                .toList();
    }

    public SessionFormationResponseDto getSessionForCurrentUser(Integer sessionId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer entrepriseId = (Integer) auth.getDetails();

        SessionFormation session = sessionRepo.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Session non trouvée"));

        if (session.getEntreprise() == null ||
                !session.getEntreprise().getIdEntreprise().equals(entrepriseId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Accès refusé à cette session");
        }

        return toSessionDto(session);
    }

    public List<EmployeResponseDto> getEmployesForCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer entrepriseId = (Integer) auth.getDetails();

        return employeRepo.findByEntreprise_IdEntreprise(entrepriseId)
                .stream()
                .map(e -> new EmployeResponseDto(
                        e.getIdEmploye(),
                        e.getNom(),
                        e.getPrenom(),
                        e.getEmail(),
                        e.getTelephone(),
                        e.getCin(),
                        e.getCnss(),
                        e.getMatricule(),
                        e.getCsp(),
                        e.getFonction(),
                        e.getTypeContrat(),
                        e.getF_h(),
                        e.getDateEmbauche(),
                        e.getDateNaissance(),
                        e.getEntreprise() != null ? e.getEntreprise().getIdEntreprise() : null,
                        e.getEntreprise() != null ? e.getEntreprise().getNomEntreprise() : null,
                        e.getDepartement() != null ? e.getDepartement().getId() : null,
                        e.getDepartement() != null ? e.getDepartement().getNom() : null
                ))
                .toList();
    }

    // ─── Get current trainer's Formateur ─────────────────────────────────────────
    private Optional<Formateur> getCurrentFormateur() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = (String) auth.getPrincipal();
        return formateurRepo.findByEmail(email);
    }

    // ─── Sessions for current trainer (assigned only) ─────────────────────────────
    public List<SessionFormationResponseDto> getSessionsForTrainer() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = (String) auth.getPrincipal();

        Formateur formateur = formateurRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Aucun formateur trouvé pour cet email. Contactez l'administrateur."));

        return sessionRepo.findByFormateur_IdFormateur(formateur.getIdFormateur())
                .stream()
                .map(this::toSessionDto)
                .toList();
    }

    public List<FormationResponseDto> getFormationsForCurrentUser() {
        Integer entrepriseId = SecurityContextUtils.getEntrepriseId();
        if (entrepriseId == null) return List.of();

        return formationRepo
                .findByEntreprise_IdEntreprise(entrepriseId)
                .stream()
                .map(f -> new FormationResponseDto(
                        f.getIdFormation(),
                        f.getModule(),
                        f.getTypeFormation(),
                        f.getFamilleFormation(),
                        f.getInterneExterne(),
                        f.getSousFamille(),
                        f.getReferenceFormation(),
                        f.getAnnee(),
                        f.getDureeHeures(),
                        f.getDureeJours(),
                        f.getPrixHeureMad(),
                        f.getPrixJourMad(),
                        f.getEntreprise().getIdEntreprise(),
                        f.getEntreprise().getNomEntreprise()
                ))
                .toList();
    }

    private void realignEvaluationScores(Integer sessionId, Integer jour, List<String> newLibelles) {
        List<SessionCritere> oldCriteres = critereRepo
                .findBySession_IdSessionAndJourOrderByCritereIndexAsc(sessionId, jour);

        if (oldCriteres.isEmpty()) return;

        List<Evaluation> evaluations = evalRepo.findBySession_IdSessionOrderByJourAsc(sessionId);

        for (Evaluation evaluation : evaluations) {
            Map<String, Integer> oldScoresByLabel = new LinkedHashMap<>();

            for (EvaluationCritere score : evaluation.getCriteres()) {
                int oldIndex = score.getCritereIndex();
                if (oldIndex >= 0 && oldIndex < oldCriteres.size()) {
                    String oldLabel = oldCriteres.get(oldIndex).getLibelle();
                    oldScoresByLabel.putIfAbsent(oldLabel, score.getScore());
                }
            }

            evaluation.getCriteres().clear();
            evalRepo.saveAndFlush(evaluation);

            for (int i = 0; i < newLibelles.size(); i++) {
                Integer oldScore = oldScoresByLabel.get(newLibelles.get(i));
                if (oldScore != null) {
                    evaluation.getCriteres().add(EvaluationCritere.builder()
                            .evaluation(evaluation)
                            .critereIndex(i)
                            .score(oldScore)
                            .build());
                }
            }

            evalRepo.save(evaluation);
        }
    }

    public DailyProgramDto getDailyProgram(Integer sessionId, Integer jour) {
        getSessionForCurrentUser(sessionId);

        return dailyProgramRepo.findBySession_IdSessionAndJour(sessionId, jour)
                .map(this::toDailyProgramDto)
                .orElseGet(() -> new DailyProgramDto(
                        null,
                        sessionId,
                        jour,
                        "",
                        List.of()
                ));
    }

    @Transactional
    public DailyProgramDto saveDailyProgram(Integer sessionId, Integer jour, DailyProgramRequest request) {
        getSessionForCurrentUser(sessionId);

        SessionFormation session = sessionRepo.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Session non trouvée"));

        SessionDailyProgram program = dailyProgramRepo
                .findBySession_IdSessionAndJour(sessionId, jour)
                .orElseGet(() -> SessionDailyProgram.builder()
                        .session(session)
                        .jour(jour)
                        .build());

        program.setCommentaire(request.commentaire());

        List<DailyProgramEntryDto> entries = request.entries() != null
                ? request.entries()
                : List.of();

        // Filter out blank entries
        List<DailyProgramEntryDto> validEntries = new ArrayList<>();
        for (DailyProgramEntryDto entry : entries) {
            String activite = entry.activite() != null ? entry.activite().trim() : "";
            if (!activite.isBlank()) validEntries.add(entry);
        }

        List<SessionDailyProgramEntry> existingEntries = program.getEntries();

        // Update existing entries in place
        for (int i = 0; i < validEntries.size(); i++) {
            DailyProgramEntryDto dto = validEntries.get(i);
            if (i < existingEntries.size()) {
                // Update existing
                SessionDailyProgramEntry existing = existingEntries.get(i);
                existing.setDateDebut(dto.dateDebut());
                existing.setDateFin(dto.dateFin());
                existing.setActivite(dto.activite().trim());
                existing.setPosition(i);
            } else {
                // Add new
                existingEntries.add(SessionDailyProgramEntry.builder()
                        .program(program)
                        .dateDebut(dto.dateDebut())
                        .dateFin(dto.dateFin())
                        .activite(dto.activite().trim())
                        .position(i)
                        .build());
            }
        }

        // Remove extras if new list is shorter
        if (existingEntries.size() > validEntries.size()) {
            existingEntries.subList(validEntries.size(), existingEntries.size()).clear();
        }

        return toDailyProgramDto(dailyProgramRepo.save(program));
    }

    private DailyProgramDto toDailyProgramDto(SessionDailyProgram program) {
        return new DailyProgramDto(
                program.getId(),
                program.getSession().getIdSession(),
                program.getJour(),
                program.getCommentaire(),
                program.getEntries().stream()
                        .map(e -> new DailyProgramEntryDto(
                                e.getId(),
                                e.getDateDebut(),
                                e.getDateFin(),
                                e.getActivite(),
                                e.getPosition()
                        ))
                        .toList()
        );
    }



    // ─── DTO mapper ───────────────────────────────────────────────────────────
    private EvaluationDto toDto(Evaluation e) {
        Map<Integer, Integer> scores = e.getCriteres().stream()
                .collect(Collectors.toMap(
                        EvaluationCritere::getCritereIndex,
                        EvaluationCritere::getScore
                ));

        double avg = scores.isEmpty() ? 0.0
                : scores.values().stream().mapToInt(i -> i).average().orElse(0.0);

        return new EvaluationDto(
                e.getId(),
                e.getSession().getIdSession(),
                e.getEmploye().getIdEmploye(),
                e.getEmploye().getNom(),
                e.getEmploye().getPrenom(),
                e.getEmploye().getMatricule(),
                e.getJour(),
                e.getPresence(),
                e.getRemarques(),
                scores,
                Math.round(avg * 100.0) / 100.0,
                e.getCreatedAt(),
                e.getDureeHeures()
        );
    }
    private SessionFormationResponseDto toSessionDto(SessionFormation s) {
        String formateurNomComplet = s.getFormateur() != null
                ? s.getFormateur().getNom() + " " + s.getFormateur().getPrenom()
                : null;

        List<ParticipantResponseDto> participants = s.getParticipations() != null
                ? s.getParticipations().stream().map(p -> {
            var e = p.getEmploye();
            return new ParticipantResponseDto(
                    e.getIdEmploye(), e.getNom(), e.getPrenom(),
                    e.getEmail(), e.getTelephone(), e.getCin(), e.getMatricule()
            );
        }).toList()
                : List.of();

        return new SessionFormationResponseDto(
                s.getIdSession(),
                s.getReferenceSession(),
                s.getFormation() != null ? s.getFormation().getIdFormation() : null,
                s.getFormation() != null ? s.getFormation().getModule() : null,
                s.getEntreprise() != null ? s.getEntreprise().getIdEntreprise() : null,
                s.getEntreprise() != null ? s.getEntreprise().getNomEntreprise() : null,
                s.getFournisseur() != null ? s.getFournisseur().getIdEntreprise() : null,
                s.getFournisseur() != null ? s.getFournisseur().getNomEntreprise() : null,
                s.getFormateur() != null ? s.getFormateur().getIdFormateur() : null,
                formateurNomComplet,
                s.getDateDebut(),
                s.getDateFin(),
                s.getDHeures(),
                s.getDJours(),
                s.getStatut(),
                participants.size(),
                participants
        );
    }
}
