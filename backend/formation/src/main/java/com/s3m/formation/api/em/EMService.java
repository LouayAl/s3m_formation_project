package com.s3m.formation.api.em;

import com.s3m.formation.api.dto.*;
import com.s3m.formation.domain.employe.Employe;
import com.s3m.formation.domain.employe.EmployeRepository;
import com.s3m.formation.domain.evaluation.Evaluation;
import com.s3m.formation.domain.evaluation.EvaluationCritere;
import com.s3m.formation.domain.evaluation.EvaluationRepository;
import com.s3m.formation.domain.sessionFormation.SessionFormation;
import com.s3m.formation.domain.sessionFormation.SessionFormationRepository;
import com.s3m.formation.domain.sessionFormation.SessionFormationStatut;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.s3m.formation.domain.sessionCritere.SessionCritere;
import com.s3m.formation.domain.sessionCritere.SessionCritereRepository;
import com.s3m.formation.domain.participation.ParticipationRepository;
import com.s3m.formation.api.dto.EmployeResponseDto;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EMService {

    private final EvaluationRepository    evalRepo;
    private final SessionFormationRepository sessionRepo;
    private final EmployeRepository       employeRepo;
    private final SessionCritereRepository critereRepo;
    private final ParticipationRepository  participationRepo;

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
                        c.getLibelle()
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

        // Delete existing criteria for that day and replace
        critereRepo.deleteBySession_IdSessionAndJour(sessionId, jour);

        List<SessionCritere> saved = new ArrayList<>();
        List<String> libelles = req.libelles();

        for (int i = 0; i < libelles.size(); i++) {
            String libelle = libelles.get(i).trim();
            if (!libelle.isBlank()) {
                saved.add(critereRepo.save(
                        SessionCritere.builder()
                                .session(session)
                                .jour(jour)
                                .critereIndex(i)
                                .libelle(libelle)
                                .build()
                ));
            }
        }

        return saved.stream()
                .map(c -> new SessionCritereDto(
                        c.getId(),
                        c.getJour(),
                        c.getCritereIndex(),
                        c.getLibelle()
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
                e.getCreatedAt()
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
                    e.getEmail(), e.getTelephone(), e.getMatricule()
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