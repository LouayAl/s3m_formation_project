package com.s3m.formation.domain.formation;

import com.s3m.formation.api.dto.FormationResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class FormationService {

    private final FormationRepository formationRepository;

    public FormationService(FormationRepository formationRepository) {
        this.formationRepository = formationRepository;
    }

    // Get all formations
    public List<FormationResponseDto> getAllFormations() {
        return formationRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    // Get by ID
    public FormationResponseDto getFormationById(Integer id) {
        Formation formation = formationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Formation not found"));
        return toDto(formation);
    }

    // Get by Reference
    public FormationResponseDto getFormationByReference(String reference) {
        Formation formation = formationRepository.findByReferenceFormation(reference)
                .orElseThrow(() -> new RuntimeException("Formation not found"));
        return toDto(formation);
    }

    // Search by module keyword
    public List<FormationResponseDto> searchFormations(String keyword) {
        return formationRepository.search(keyword)
                .stream()
                .map(this::toDto)
                .toList();
    }

    // Filter by multiple criteria
    public List<FormationResponseDto> filterFormations(
            String module,
            String famille,
            String type,
            String sousFamille
    ) {
        return formationRepository.search(
                        module,
                        type,
                        famille,
                        sousFamille
                )
                .stream()
                .map(this::toDto)
                .toList();
    }

    // Create new formation
    public FormationResponseDto createFormation(Formation formation) {
        Formation saved = formationRepository.save(formation);
        return toDto(saved);
    }

    // Update formation
    public FormationResponseDto updateFormation(Integer id, Formation updated) {
        Formation existing = formationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Formation not found"));

        existing.setModule(updated.getModule());
        existing.setTypeFormation(updated.getTypeFormation());
        existing.setFamilleFormation(updated.getFamilleFormation());
        existing.setSousFamille(updated.getSousFamille());
        existing.setInterneExterne(updated.getInterneExterne());
        existing.setReferenceFormation(updated.getReferenceFormation());
        existing.setAnnee(updated.getAnnee());
        existing.setPrixHeureMad(updated.getPrixHeureMad());
        existing.setPrixJourMad(updated.getPrixJourMad());
        existing.setDureeHeures(updated.getDureeHeures());
        existing.setDureeJours(updated.getDureeJours());

        Formation saved = formationRepository.save(existing);
        return toDto(saved);
    }

    // Delete formation
    public void deleteFormation(Integer id) {
        formationRepository.deleteById(id);
    }

    /* =========================
       Mapping (temporary)
       ========================= */
    private FormationResponseDto toDto(Formation formation) {
        return new FormationResponseDto(
                formation.getIdFormation(),
                formation.getModule(),
                formation.getTypeFormation(),
                formation.getFamilleFormation(),
                formation.getSousFamille(),
                formation.getReferenceFormation(),
                formation.getAnnee(),
                formation.getDureeHeures(),
                formation.getDureeJours(),
                formation.getPrixHeureMad(),
                formation.getPrixJourMad()
        );
    }
}
