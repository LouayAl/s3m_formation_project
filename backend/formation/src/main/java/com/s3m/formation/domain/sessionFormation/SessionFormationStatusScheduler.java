package com.s3m.formation.domain.sessionFormation;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionFormationStatusScheduler {

    private final SessionFormationRepository sessionRepository;

    @Scheduled(cron = "0 0 0 * * *") // every day at midnight
    @Transactional
    public void updateSessionStatuses() {

        LocalDate today = LocalDate.now();

        List<SessionFormation> sessions = sessionRepository.findAll();

        for (SessionFormation session : sessions) {

            // Ignore cancelled or finished sessions
            if (session.getStatut() == SessionFormationStatut.ANNULEE ||
                    session.getStatut() == SessionFormationStatut.TERMINEE) {
                continue;
            }

            // ✅ Start session automatically
            if (session.getStatut() == SessionFormationStatut.PLANIFIEE &&
                    !session.getDateDebut().isAfter(today)) {

                session.setStatut(SessionFormationStatut.EN_COURS);
            }

            // ✅ End session automatically the day after dateFin
            if (session.getStatut() == SessionFormationStatut.EN_COURS &&
                    session.getDateFin().isBefore(today)) {

                session.setStatut(SessionFormationStatut.TERMINEE);
            }
        }
    }
}
