package com.s3m.formation.domain.reservation.admin;

import com.s3m.formation.api.dto.PlanifierDemandeRequest;
import com.s3m.formation.domain.reservation.DemandeReservation;
import com.s3m.formation.domain.reservation.DemandeReservationRepository;
import com.s3m.formation.domain.reservation.DemandeReservationStatut;
import com.s3m.formation.domain.sessionFormation.SessionFormation;
import com.s3m.formation.domain.sessionFormation.SessionFormationRepository;
import com.s3m.formation.domain.sessionFormation.SessionFormationStatut;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
@RequiredArgsConstructor
@Transactional
public class DemandeReservationAdminService {

    private final DemandeReservationRepository demandeRepository;
    private final SessionFormationRepository sessionRepository;

    /* =========================
       VALIDATE
       ========================= */

    public void validateDemande(Integer demandeId) {
        DemandeReservation demande = load(demandeId);
        ensureStatus(demande, DemandeReservationStatut.EN_ATTENTE);

        demande.setStatut(DemandeReservationStatut.VALIDEE);
    }

    /* =========================
       REFUSE
       ========================= */

    public void refuseDemande(Integer demandeId, String reason) {
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Refusal reason is required");
        }

        DemandeReservation demande = load(demandeId);
        ensureStatus(demande, DemandeReservationStatut.EN_ATTENTE);

        demande.setStatut(DemandeReservationStatut.REFUSEE);
        demande.setCommentaireAdmin(reason);
    }

    /* =========================
       CANCEL
       ========================= */

    public void cancelDemande(Integer demandeId) {
        DemandeReservation demande = load(demandeId);

        if (demande.getStatut() != DemandeReservationStatut.EN_ATTENTE &&
                demande.getStatut() != DemandeReservationStatut.VALIDEE) {
            throw new IllegalStateException(
                    "Only EN_ATTENTE or VALIDEE demandes can be cancelled"
            );
        }

        demande.setStatut(DemandeReservationStatut.ANNULEE);
    }

    /* =========================
       PLANIFIER (CREATE SESSION)
       ========================= */

    @Transactional
    public void planifier(Integer demandeId, PlanifierDemandeRequest request) {

        DemandeReservation demande = load(demandeId);

        // 1️⃣ Status validation
        if (demande.getStatut() != DemandeReservationStatut.VALIDEE) {
            throw new IllegalStateException(
                    "Only VALIDEE reservations can be planned"
            );
        }

        // 2️⃣ Avoid double planning (1 demande = 1 session)
        if (sessionRepository.existsByDemande(demande)) {
            throw new IllegalStateException(
                    "This demande already has a session"
            );
        }

        // 3️⃣ Validate planning dates
        if (request.getDateDebut() == null || request.getDateFin() == null) {
            throw new IllegalArgumentException("Session dates are required");
        }

        if (request.getDateDebut().isAfter(request.getDateFin())) {
            throw new IllegalArgumentException(
                    "dateDebut must be before or equal to dateFin"
            );
        }

        // 4️⃣ Create session (Session is the aggregate owner)
        SessionFormation session = SessionFormation.builder()
                .demande(demande)
                .formation(demande.getFormation())
                .dateDebut(request.getDateDebut())
                .dateFin(request.getDateFin())
                .statut(SessionFormationStatut.PLANIFIEE)
                .build();

        sessionRepository.save(session);

        // 5️⃣ Optional admin comment
        demande.setCommentaireAdmin(request.getCommentaireAdmin());
    }


    /* =========================
       HELPERS
       ========================= */

    private DemandeReservation load(Integer id) {
        return demandeRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Demande not found"));
    }

    private void ensureStatus(
            DemandeReservation demande,
            DemandeReservationStatut expected
    ) {
        if (demande.getStatut() != expected) {
            throw new IllegalStateException(
                    "Invalid status transition from " + demande.getStatut()
            );
        }
    }
}
