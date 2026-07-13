package com.s3m.formation.domain.formation;

import com.s3m.formation.api.dto.FormationResponseDto;
import com.s3m.formation.domain.entreprise.Entreprise;
import com.s3m.formation.security.util.SecurityContextUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FormationService {

    private final FormationRepository formationRepository;
    private final EntityManager entityManager;
    // =========================
    // GET ALL (SCOPED, ADMIN CAN FILTER)
    // =========================
    public List<FormationResponseDto> getVisibleFormationsForCurrentUser(Integer requestedEntrepriseId) {
        if (currentUserIsAdminOnly()) {
            // ADMIN: requestedEntrepriseId == null -> every formation in the DB
            List<Formation> formations = (requestedEntrepriseId == null)
                    ? formationRepository.findAll()
                    : formationRepository.findByEntreprise_IdEntreprise(requestedEntrepriseId);
            return formations.stream().map(this::toDto).toList();
        }

        // Everyone else: always scoped to their own entreprise, ignore requestedEntrepriseId
        Integer entrepriseId = SecurityContextUtils.getEntrepriseId();
        if (entrepriseId == null) return List.of();

        return formationRepository.findByEntreprise_IdEntreprise(entrepriseId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    // =========================
    // SEARCH
    // =========================
    public List<FormationResponseDto> searchFormations(String keyword) {
        Integer entrepriseId = SecurityContextUtils.getEntrepriseId();
        if (entrepriseId == null) return List.of();

        return formationRepository.search(keyword, entrepriseId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    // =========================
    // FILTER
    // =========================
    public List<FormationResponseDto> filterFormations(String module,
                                                       String famille,
                                                       String type,
                                                       String sousFamille) {
        Integer entrepriseId = SecurityContextUtils.getEntrepriseId();
        if (entrepriseId == null) return List.of();

        return formationRepository.searchAdvanced(
                        module, type, famille, sousFamille, entrepriseId
                )
                .stream()
                .map(this::toDto)
                .toList();
    }

    // =========================
    // GET BY ID (SECURITY CHECK — ADMIN BYPASSES)
    // =========================
    public FormationResponseDto getFormationById(Integer id) {
        Formation formation = formationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Formation not found"));

        if (!currentUserIsAdminOnly()) {
            Integer entrepriseId = SecurityContextUtils.getEntrepriseId();
            if (!formation.getEntreprise().getIdEntreprise().equals(entrepriseId)) {
                throw new RuntimeException("Access denied");
            }
        }

        return toDto(formation);
    }

    // =========================
    // CREATE
    // =========================
    public FormationResponseDto createFormation(Formation formation) {
        Integer entrepriseId = SecurityContextUtils.getEntrepriseId();

        formation.setEntreprise(
                entityManager.getReference(Entreprise.class, entrepriseId)
        );

        // force override
        formation.setIdFormation(null);

        return toDto(formationRepository.save(formation));
    }

    // =========================
    // UPDATE (SECURITY CHECK — ADMIN BYPASSES)
    // =========================
    public FormationResponseDto updateFormation(Integer id, Formation updated) {

        Formation formation = formationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Formation not found"));

        if (!currentUserIsAdminOnly()) {
            Integer entrepriseId = SecurityContextUtils.getEntrepriseId();
            if (!formation.getEntreprise().getIdEntreprise().equals(entrepriseId)) {
                throw new RuntimeException("Access denied");
            }
        }

        formation.setModule(updated.getModule());
        formation.setTypeFormation(updated.getTypeFormation());
        formation.setFamilleFormation(updated.getFamilleFormation());
        formation.setSousFamille(updated.getSousFamille());
        formation.setInterneExterne(updated.getInterneExterne());
        formation.setAnnee(updated.getAnnee());
        formation.setReferenceFormation(updated.getReferenceFormation());
        formation.setDureeHeures(updated.getDureeHeures());
        formation.setDureeJours(updated.getDureeJours());
        formation.setPrixHeureMad(updated.getPrixHeureMad());
        formation.setPrixJourMad(updated.getPrixJourMad());

        return toDto(formationRepository.save(formation));
    }

    // =========================
    // DELETE (SECURITY CHECK — ADMIN BYPASSES)
    // =========================
    public void deleteFormation(Integer id) {
        Formation formation = formationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Formation not found"));

        if (!currentUserIsAdminOnly()) {
            Integer entrepriseId = SecurityContextUtils.getEntrepriseId();
            if (!formation.getEntreprise().getIdEntreprise().equals(entrepriseId)) {
                throw new RuntimeException("Access denied");
            }
        }

        formationRepository.delete(formation);
    }

    // =========================
    // MAPPER
    // =========================
    private FormationResponseDto toDto(Formation f) {
        return new FormationResponseDto(
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
        );
    }

    // =========================
    // ROLE HELPER
    // =========================
    private boolean currentUserIsAdminOnly() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;

        for (GrantedAuthority authority : auth.getAuthorities()) {
            if ("ADMIN".equals(authority.getAuthority())) {
                return true;
            }
        }
        return false;
    }
}