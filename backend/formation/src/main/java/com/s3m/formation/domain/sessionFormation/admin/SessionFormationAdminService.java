package com.s3m.formation.domain.sessionFormation.admin;


import com.s3m.formation.domain.entreprise.Entreprise;
import com.s3m.formation.domain.entreprise.EntrepriseRepository;
import com.s3m.formation.domain.formateur.Formateur;
import com.s3m.formation.domain.formateur.FormateurRepository;
import com.s3m.formation.domain.sessionFormation.SessionFormation;
import com.s3m.formation.domain.sessionFormation.SessionFormationRepository;
import com.s3m.formation.domain.sessionFormation.admin.dto.SessionFormationAdminUpdateRequest;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SessionFormationAdminService {

    private final SessionFormationRepository sessionRepository;
    private final FormateurRepository formateurRepository;
    private final EntrepriseRepository entrepriseRepository;

    public void updateSession(
            Integer sessionId,
            SessionFormationAdminUpdateRequest request
    ) {

        SessionFormation session = sessionRepository.findById(sessionId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Session not found"));
        Integer previousFormateurId = session.getFormateur() != null ? session.getFormateur().getIdFormateur() : null;
        Integer previousSupplierId = session.getFournisseur() != null ? session.getFournisseur().getIdEntreprise() : null;

        /* =========================
           UPDATE FORMATEUR
           ========================= */
        if (request.getFormateurId() != null) {
            Formateur formateur = formateurRepository.findById(request.getFormateurId())
                    .orElseThrow(() ->
                            new IllegalArgumentException("Formateur not found"));

            session.setFormateur(formateur);
        }

        /* =========================
           UPDATE FOURNISSEUR
           ========================= */
        if (request.getFournisseurId() != null) {
            Entreprise fournisseur = entrepriseRepository.findById(request.getFournisseurId())
                    .orElseThrow(() ->
                            new IllegalArgumentException("Fournisseur not found"));

            session.setFournisseur(fournisseur);
        }

        /* =========================
           UPDATE DATES
           ========================= */
        if (request.getDateDebut() != null) {
            session.setDateDebut(request.getDateDebut());
        }

        if (request.getDateFin() != null) {
            session.setDateFin(request.getDateFin());
        }

        // Business validation
        if (session.getDateDebut() != null
                && session.getDateFin() != null
                && session.getDateDebut().isAfter(session.getDateFin())) {

            throw new IllegalStateException(
                    "dateDebut must be before or equal to dateFin");
        }

        Integer currentFormateurId = session.getFormateur() != null ? session.getFormateur().getIdFormateur() : null;
        Integer currentSupplierId = session.getFournisseur() != null ? session.getFournisseur().getIdEntreprise() : null;
        boolean assignmentChanged = !java.util.Objects.equals(previousFormateurId, currentFormateurId)
                || !java.util.Objects.equals(previousSupplierId, currentSupplierId);
        boolean supplierOutsideS3m = session.getFournisseur() != null
                && session.getFournisseur().getNomEntreprise() != null
                && !"S3M".equalsIgnoreCase(session.getFournisseur().getNomEntreprise().trim());

        if (supplierOutsideS3m && session.getFormateur() != null) {
            if (!Boolean.TRUE.equals(session.getFormateurConfirme())) {
                session.setFormateurConfirme(true);
                session.setFormateurConfirmeLe(LocalDateTime.now());
                session.setFormateurConfirmePar("SYSTEM");
            }
        } else if (assignmentChanged) {
            session.setFormateurConfirme(false);
            session.setFormateurConfirmeLe(null);
            session.setFormateurConfirmePar(null);
            session.setNotificationEnvoyeeLe(null);
        }
    }

}
