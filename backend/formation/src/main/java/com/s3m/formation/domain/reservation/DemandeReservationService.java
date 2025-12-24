package com.s3m.formation.domain.reservation;

import com.s3m.formation.api.dto.DemandeReservationResponse;
import com.s3m.formation.domain.entreprise.Entreprise;
import com.s3m.formation.domain.entreprise.EntrepriseRepository;
import com.s3m.formation.domain.fiche.FicheStatut;
import com.s3m.formation.domain.fiche.FicheTechniqueFormation;
import com.s3m.formation.domain.fiche.FicheTechniqueFormationRepository;
import com.s3m.formation.domain.formation.Formation;
import com.s3m.formation.domain.formation.FormationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class DemandeReservationService {

    private final DemandeReservationRepository repository;
    private final EntrepriseRepository entrepriseRepository;
    private final FormationRepository formationRepository;
    private final FicheTechniqueFormationRepository ficheRepository;

    public DemandeReservationResponse create(
            DemandeReservationRequest request,
            Integer entrepriseId
    ) {
        // 1️⃣ Validate input dates
        validateDates(
                request.getDateDebutSouhaitee(),
                request.getDateFinSouhaitee()
        );

        // 2️⃣ Load entreprise
        Entreprise entreprise = entrepriseRepository.findById(entrepriseId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Entreprise not found"));

        // 3️⃣ Load formation
        Formation formation = formationRepository.findById(request.getIdFormation())
                .orElseThrow(() ->
                        new IllegalArgumentException("Formation not found"));

        // 4️⃣ Auto-select ACTIVE fiche technique (latest version)
        FicheTechniqueFormation fiche = ficheRepository
                .findFirstByFormation_IdFormationAndStatutOrderByVersionNumeroDesc(
                        formation.getIdFormation(),
                        FicheStatut.ACTIVE
                )
                .orElseThrow(() ->
                        new IllegalStateException(
                                "No ACTIVE fiche technique found for formation "
                                        + formation.getIdFormation()
                        )
                );

        // 5️⃣ Build demande reservation
        DemandeReservation demande = DemandeReservation.builder()
                .entreprise(entreprise)
                .formation(formation)
                .ficheTechnique(fiche)
                .dateDebutSouhaitee(request.getDateDebutSouhaitee())
                .dateFinSouhaitee(request.getDateFinSouhaitee())
                .commentaireClient(request.getCommentaireClient())
                .build();

        // 6️⃣ Persist
        DemandeReservation saved = repository.save(demande);

        // 7️⃣ Return response DTO
        return new DemandeReservationResponse(
                saved.getIdDemande(),
                saved.getStatut(),
                saved.getDateDebutSouhaitee(),
                saved.getDateFinSouhaitee()
        );
    }

    /**
     * Business validation for requested dates
     */
    private void validateDates(LocalDate debut, LocalDate fin) {
        if (debut == null || fin == null) {
            throw new IllegalArgumentException("Requested dates must not be null");
        }

        if (debut.isAfter(fin)) {
            throw new IllegalArgumentException(
                    "dateDebutSouhaitee must be before or equal to dateFinSouhaitee"
            );
        }
    }


}
