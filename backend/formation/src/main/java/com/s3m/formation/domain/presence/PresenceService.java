package com.s3m.formation.domain.presence;

import com.s3m.formation.domain.participation.Participation;
import com.s3m.formation.domain.participation.ParticipationRepository;
import com.s3m.formation.domain.sessionFormation.SessionFormation;
import com.s3m.formation.domain.sessionFormation.SessionFormationRepository;
import com.s3m.formation.security.util.AuthDetails;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class PresenceService {

    private final PresenceRepository      presenceRepo;
    private final ParticipationRepository participationRepo;
    private final SessionFormationRepository sessionRepo;

    public PresenceService(PresenceRepository presenceRepo,
                           ParticipationRepository participationRepo,
                           SessionFormationRepository sessionRepo) {
        this.presenceRepo      = presenceRepo;
        this.participationRepo = participationRepo;
        this.sessionRepo       = sessionRepo;
    }

    // ── GET: presence for a given day ─────────────────────────────────────────
    @Transactional(readOnly = true)
    public PresenceJourResponse getPresenceForDay(Integer sessionId, LocalDate jour) {
        ensureVisibleToCurrentUser(sessionId);

        // Load all participations for the session
        List<Participation> participations = participationRepo
                .findBySession_IdSession(sessionId);

        // Load existing presence records for that day (may be empty)
        List<Presence> existing = presenceRepo.findBySessionAndJour(sessionId, jour);
        Map<Integer, Boolean> presenceMap = existing.stream()
                .collect(Collectors.toMap(
                        p -> p.getParticipation().getIdParticipation(),
                        Presence::getPresent
                ));

        List<PresenceJourDto> dtos = participations.stream()
                .filter(this::visibleParticipation)
                .map(part -> {
                    var emp = part.getEmploye();
                    return new PresenceJourDto(
                            part.getIdParticipation(),
                            emp.getIdEmploye(),
                            emp.getNom(),
                            emp.getPrenom(),
                            emp.getCin(),
                            emp.getMatricule(),
                            emp.getDepartement() != null ? emp.getDepartement().getNom() : null,
                            presenceMap.get(part.getIdParticipation()) // null if not yet recorded
                    );
                })
                .toList();

        return new PresenceJourResponse(sessionId, jour, dtos);
    }

    // ── GET: all days that have presence records ───────────────────────────────
    @Transactional(readOnly = true)
    public PresenceDaysResponse getRecordedDays(Integer sessionId) {
        ensureVisibleToCurrentUser(sessionId);
        List<LocalDate> jours = presenceRepo.findJoursBySession(sessionId);
        return new PresenceDaysResponse(sessionId, jours);
    }

    // ── GET: valid session days (dateDebut → dateFin) ──────────────────────────
    @Transactional(readOnly = true)
    public List<LocalDate> getSessionDays(Integer sessionId) {
        SessionFormation session = sessionRepo.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Session non trouvée"));

        ensureVisibleToCurrentUser(sessionId);
        if (session.getDateDebut() == null || session.getDateFin() == null) return List.of();

        List<LocalDate> days = new ArrayList<>();
        LocalDate d = session.getDateDebut();
        while (!d.isAfter(session.getDateFin())) {
            days.add(d);
            d = d.plusDays(1);
        }
        return days;
    }

    // ── POST: save / upsert full day presence ─────────────────────────────────
    public PresenceJourResponse savePresence(Integer sessionId, SavePresenceRequest req) {

        // Validate session exists
        if (!sessionRepo.existsById(sessionId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Session non trouvée");
        }

        ensureVisibleToCurrentUser(sessionId);
        for (SavePresenceRequest.PresenceEntry entry : req.presences()) {
            Participation participation = participationRepo
                    .findById(entry.participationId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Participation non trouvée : " + entry.participationId()));

            // Verify this participation belongs to the requested session
            if (!participation.getSession().getIdSession().equals(sessionId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Participation " + entry.participationId() + " n'appartient pas à cette session.");
            }

            if (!visibleParticipation(participation)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Vous pouvez uniquement gérer la présence des employés de votre département.");
            }

            // Upsert
            Presence presence = presenceRepo
                    .findByParticipation_IdParticipationAndJour(entry.participationId(), req.jour())
                    .orElseGet(() -> Presence.builder()
                            .participation(participation)
                            .jour(req.jour())
                            .build());

            presence.setPresent(entry.present() != null && entry.present());
            presenceRepo.save(presence);
        }

        return getPresenceForDay(sessionId, req.jour());
    }

    private boolean isDepartmentChef() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> "CHEF_DEPARTEMENT".equals(a.getAuthority()));
    }

    private boolean visibleParticipation(Participation participation) {
        if (!isDepartmentChef()) return true;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        AuthDetails details = auth != null && auth.getDetails() instanceof AuthDetails d ? d : null;
        return details != null && details.getDepartementId() != null
                && participation.getEmploye() != null && participation.getEmploye().getDepartement() != null
                && details.getDepartementId().equals(participation.getEmploye().getDepartement().getId())
                && details.getEntrepriseId() != null && participation.getEmploye().getEntreprise() != null
                && details.getEntrepriseId().equals(participation.getEmploye().getEntreprise().getIdEntreprise());
    }

    private void ensureVisibleToCurrentUser(Integer sessionId) {
        if (!isDepartmentChef()) return;
        boolean hasOwnParticipant = participationRepo.findBySession_IdSession(sessionId).stream()
                .anyMatch(this::visibleParticipation);
        if (!hasOwnParticipant) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Session non trouvée pour ce département.");
        }
    }
}
