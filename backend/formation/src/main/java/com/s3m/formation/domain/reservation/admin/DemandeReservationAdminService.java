package com.s3m.formation.domain.reservation.admin;

import com.s3m.formation.api.dto.PlanifierDemandeRequest;
import com.s3m.formation.domain.reservation.DemandeReservation;
import com.s3m.formation.domain.reservation.DemandeReservationRepository;
import com.s3m.formation.domain.reservation.DemandeReservationStatut;
import com.s3m.formation.domain.sessionFormation.SessionFormation;
import com.s3m.formation.domain.sessionFormation.SessionFormationRepository;
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

    public void planifier(Integer demandeId, PlanifierDemandeRequest request) {

        DemandeReservation demande = load(demandeId);

        if (demande.getStatut() != DemandeReservationStatut.VALIDEE) {
            throw new IllegalStateException(
                    "Only VALIDEE reservations can be planned"
            );
        }

        // Optional safety check (avoid double session)
        if (sessionRepository.existsByDemande(demande)) {
            throw new IllegalStateException(
                    "This demande already has a session"
            );
        }

        SessionFormation session = SessionFormation.builder()
                .demande(demande)
                .formation(demande.getFormation())
                .dateDebut(request.getDateDebut())
                .dateFin(request.getDateFin())
                .statut("PLANIFIEE")
                .build();

        sessionRepository.save(session);

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
