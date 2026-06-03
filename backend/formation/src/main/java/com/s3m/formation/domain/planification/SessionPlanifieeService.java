package com.s3m.formation.domain.planification;

import com.s3m.formation.domain.entreprise.Entreprise;
import com.s3m.formation.domain.entreprise.EntrepriseRepository;
import com.s3m.formation.domain.sessionFormation.SessionFormation;
import com.s3m.formation.domain.sessionFormation.SessionFormationRepository;
import com.s3m.formation.domain.sessionFormation.SessionFormationStatut;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class SessionPlanifieeService {

    private static final BigDecimal DEFAULT_HEURES = BigDecimal.valueOf(8);

    private final SessionPlanifieeRepository planRepo;
    private final EntrepriseRepository       entrepriseRepo;
    private final SessionFormationRepository sessionRepo;

    public SessionPlanifieeService(SessionPlanifieeRepository planRepo,
                                   EntrepriseRepository entrepriseRepo,
                                   SessionFormationRepository sessionRepo) {
        this.planRepo       = planRepo;
        this.entrepriseRepo = entrepriseRepo;
        this.sessionRepo    = sessionRepo;
    }

    // ── GET full year view ────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public PlanificationAnnuelleResponse getYear(int annee, Integer entrepriseId) {
        List<SessionPlanifiee> planned = planRepo.findByEntrepriseAndYear(entrepriseId, annee);

        List<SessionPlanifieeDto> dtos = planned.stream()
                .map(s -> new SessionPlanifieeDto(s.getId(), s.getDateSession(), s.getDHeures(), s.getNotes()))
                .toList();

        List<PlanificationAnnuelleResponse.MonthSummary> plannedSummary = aggregateToMonths(planned);
        List<PlanificationAnnuelleResponse.MonthSummary> actualSummary  = computeActual(annee, entrepriseId);

        return new PlanificationAnnuelleResponse(annee, entrepriseId, dtos, plannedSummary, actualSummary,false);
    }

    // ── BULK ADD ──────────────────────────────────────────────────────────────
    public PlanificationAnnuelleResponse bulkAdd(BulkAddRequest req) {
        if (req.count() < 1 || req.count() > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Le nombre de sessions doit être compris entre 1 et 100.");
        }

        Entreprise entreprise = entrepriseRepo.findById(req.entrepriseId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Entreprise non trouvée : " + req.entrepriseId()));

        BigDecimal heures = req.dHeures() != null && req.dHeures().compareTo(BigDecimal.ZERO) > 0
                ? req.dHeures()
                : DEFAULT_HEURES;

        List<SessionPlanifiee> toSave = new ArrayList<>();
        for (int i = 0; i < req.count(); i++) {
            toSave.add(SessionPlanifiee.builder()
                    .entreprise(entreprise)
                    .dateSession(req.dateSession())
                    .dHeures(heures)
                    .notes(req.notes())
                    .build());
        }
        planRepo.saveAll(toSave);

        int affectedYear = req.dateSession().getYear();

        PlanificationAnnuelleResponse response =
                getYear(affectedYear, req.entrepriseId());

        return new PlanificationAnnuelleResponse(
                response.annee(),
                response.entrepriseId(),
                response.sessions(),
                response.planned(),
                response.actual(),
                true
        );
    }

    // ── UPDATE single session ─────────────────────────────────────────────────
    public PlanificationAnnuelleResponse update(Integer id, Integer entrepriseId,
                                                SessionPlanifieeUpdateRequest req) {
        SessionPlanifiee session = planRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Session planifiée non trouvée : " + id));

        if (!session.getEntreprise().getIdEntreprise().equals(entrepriseId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès refusé.");
        }

        session.setDateSession(req.dateSession());
        session.setDHeures(req.dHeures() != null ? req.dHeures() : DEFAULT_HEURES);
        session.setNotes(req.notes());
        planRepo.save(session);

        int affectedYear = req.dateSession().getYear();

        PlanificationAnnuelleResponse response =
                getYear(affectedYear, entrepriseId);

        return new PlanificationAnnuelleResponse(
                response.annee(),
                response.entrepriseId(),
                response.sessions(),
                response.planned(),
                response.actual(),
                true
        );
    }

    // ── DELETE single session ─────────────────────────────────────────────────
    public void delete(Integer id, Integer entrepriseId) {
        SessionPlanifiee session = planRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Session planifiée non trouvée : " + id));

        if (!session.getEntreprise().getIdEntreprise().equals(entrepriseId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès refusé.");
        }
        planRepo.deleteById(id);
    }

    // ── Aggregate planned sessions into 12 monthly buckets ───────────────────
    private List<PlanificationAnnuelleResponse.MonthSummary> aggregateToMonths(
            List<SessionPlanifiee> sessions) {

        // Group by month (1-based)
        Map<Integer, List<SessionPlanifiee>> byMonth = sessions.stream()
                .collect(Collectors.groupingBy(s -> s.getDateSession().getMonthValue()));

        List<PlanificationAnnuelleResponse.MonthSummary> result = new ArrayList<>();
        for (int m = 1; m <= 12; m++) {
            List<SessionPlanifiee> list = byMonth.getOrDefault(m, List.of());
            double heures = list.stream()
                    .mapToDouble(s -> s.getDHeures().doubleValue())
                    .sum();
            result.add(new PlanificationAnnuelleResponse.MonthSummary(
                    m, monthLabel(m), list.size(), Math.round(heures * 100.0) / 100.0
            ));
        }
        return result;
    }

    // ── Compute actual (TERMINEE) sessions per month ──────────────────────────
    private List<PlanificationAnnuelleResponse.MonthSummary> computeActual(
            int annee, Integer entrepriseId) {

        // TODO: filter by statut (e.g. TERMINEE) once workflow is confirmed
        // For now we count all sessions that exist regardless of status
        List<SessionFormation> done = sessionRepo.findTermineesForEntrepriseAndYear(
                entrepriseId,
                LocalDate.of(annee, 1, 1),
                LocalDate.of(annee, 12, 31)
        );

        double[] hours  = new double[13];
        int[]    counts = new int[13];

        for (SessionFormation s : done) {
            if (s.getDateDebut() == null) continue;
            int m = s.getDateDebut().getMonthValue();
            counts[m]++;
            if (s.getDHeures() != null) hours[m] += s.getDHeures().doubleValue();
        }

        List<PlanificationAnnuelleResponse.MonthSummary> result = new ArrayList<>();
        for (int m = 1; m <= 12; m++) {
            result.add(new PlanificationAnnuelleResponse.MonthSummary(
                    m, monthLabel(m), counts[m], Math.round(hours[m] * 100.0) / 100.0
            ));
        }
        return result;
    }

    private String monthLabel(int month) {
        return Month.of(month).getDisplayName(TextStyle.FULL, Locale.FRENCH);
    }
}