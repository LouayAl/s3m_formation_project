package com.s3m.formation.domain.sessionFormation;

import com.s3m.formation.api.exception.SessionFormationException;
import com.s3m.formation.domain.sessionFormation.sessionFormationAudit.SessionFormationAudit;
import com.s3m.formation.domain.sessionFormation.sessionFormationAudit.SessionFormationAuditRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionFormationStatusScheduler {

    private static final Logger log = LoggerFactory.getLogger(SessionFormationStatusScheduler.class);

    // Recorded as the "modifiePar" actor in the audit trail for automatic
    // transitions, so an admin can tell a scheduled change apart from a manual one.
    private static final String SYSTEM_ACTOR = "SYSTEM (auto)";

    private final SessionFormationRepository sessionRepository;
    private final SessionFormationAuditRepository auditRepository;

    // Run once at startup too, so a restart/redeploy that happens to skip a
    // scheduled window doesn't leave sessions stale until the next tick.
    //
    // IMPORTANT: this must never throw. An exception out of a @PostConstruct
    // method aborts the whole ApplicationContext refresh and the app fails to
    // boot entirely — one bad row shouldn't be able to take the server down.
    @PostConstruct
    public void onStartup() {
        log.info("Vérification initiale des statuts de session au démarrage de l'application...");
        try {
            updateSessionStatuses();
        } catch (Exception e) {
            log.error("La vérification des statuts de session au démarrage a échoué — l'application démarre quand même, mais ce point mérite d'être corrigé.", e);
        }
    }

    // Every 15 minutes — daily-only cadence left sessions showing PLANIFIEE
    // all day even after their actual start time, since it only ran at
    // midnight. This keeps the status pill in the UI close to real time
    // without needing anyone to click "Démarrer" manually.
    @Scheduled(cron = "0 */15 * * * *")
    @Transactional
    public void updateSessionStatuses() {

        LocalDate today = LocalDate.now();

        // Only sessions that could still transition — TERMINEE/ANNULEE are final states.
        List<SessionFormation> sessions = sessionRepository.findByStatutIn(
                List.of(SessionFormationStatut.PLANIFIEE, SessionFormationStatut.EN_COURS)
        );

        int started = 0, finished = 0, skipped = 0, malformed = 0;

        for (SessionFormation session : sessions) {

            // Defensive: a session shouldn't be able to reach PLANIFIEE/EN_COURS
            // without valid dates, but the data clearly isn't guaranteed to be
            // clean (bad manual edit, partial import, etc.) — don't let one
            // malformed row throw and abort the whole batch.
            if (session.getStatut() == SessionFormationStatut.PLANIFIEE &&
                    session.getDateDebut() == null) {
                log.warn("Session (id={}) est PLANIFIEE mais n'a pas de date de début — ignorée par le scheduler.",
                        session.getIdSession());
                malformed++;
                continue;
            }
            if (session.getStatut() == SessionFormationStatut.EN_COURS &&
                    session.getDateFin() == null) {
                log.warn("Session (id={}) est EN_COURS mais n'a pas de date de fin — ignorée par le scheduler.",
                        session.getIdSession());
                malformed++;
                continue;
            }

            // ✅ Start session automatically — routed through the entity's own
            // demarrer() so an automatic transition can never produce a state
            // that the manual "Démarrer" action would have rejected (e.g. a
            // session missing its formateur/fournisseur).
            if (session.getStatut() == SessionFormationStatut.PLANIFIEE &&
                    !session.getDateDebut().isAfter(today)) {

                SessionFormationStatut avant = session.getStatut();
                try {
                    session.demarrer(today);
                    auditTransition(session, avant, session.getStatut());
                    started++;
                } catch (SessionFormationException e) {
                    // Missing formateur/fournisseur, etc. — leave it PLANIFIEE rather
                    // than force an inconsistent EN_COURS state; surface it in the logs
                    // so someone can go fix the missing data.
                    log.warn("Session {} (id={}) n'a pas pu démarrer automatiquement : {}",
                            session.getReferenceSession(), session.getIdSession(), e.getMessage());
                    skipped++;
                }
                continue;
            }

            // ✅ End session automatically once dateFin has fully passed
            // (stays EN_COURS through its actual last day, terminates the day after).
            if (session.getStatut() == SessionFormationStatut.EN_COURS &&
                    session.getDateFin().isBefore(today)) {

                SessionFormationStatut avant = session.getStatut();
                session.terminer();
                auditTransition(session, avant, session.getStatut());
                finished++;
            }
        }

        if (started > 0 || finished > 0 || skipped > 0 || malformed > 0) {
            log.info("Mise à jour automatique des statuts de session : {} démarrée(s), {} terminée(s), {} en attente (préconditions manquantes), {} ignorée(s) (données incomplètes).",
                    started, finished, skipped, malformed);
        }
    }

    private void auditTransition(SessionFormation session,
                                 SessionFormationStatut avant,
                                 SessionFormationStatut apres) {
        SessionFormationAudit audit = SessionFormationAudit.builder()
                .session(session)
                .statutAvant(avant)
                .statutApres(apres)
                .modifiePar(SYSTEM_ACTOR)
                .dateModification(LocalDateTime.now())
                .build();

        auditRepository.save(audit);
    }
}