package com.s3m.formation.domain.formation;

import com.s3m.formation.api.dto.FormationResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

//Mapping is inside service for now
//Later we’ll move it to a Mapper (MapStruct)

@Service
public class FormationService {

    private final FormationRepository formationRepository;

    public FormationService(FormationRepository formationRepository) {
        this.formationRepository = formationRepository;
    }

    public List<FormationResponseDto> getAllFormations() {
        return formationRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public FormationResponseDto getFormationById(Integer id) {
        Formation formation = formationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Formation not found"));

        return toDto(formation);
    }

    public FormationResponseDto getFormationByReference(String reference) {
        Formation formation = formationRepository.findByReferenceFormation(reference)
                .orElseThrow(() -> new RuntimeException("Formation not found"));

        return toDto(formation);
    }

    public List<FormationResponseDto> searchFormations(String keyword) {
        return formationRepository.search(keyword)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<FormationResponseDto> filterFormations(
            String famille,
            String type,
            Integer annee
    ) {
        return formationRepository
                .filter(famille, type, annee)
                .stream()
                .map(this::toDto)
                .toList();
    }

    /* =========================
       MAPPING (temporary)
       ========================= */
    private FormationResponseDto toDto(Formation formation) {
        return new FormationResponseDto(
                formation.getIdFormation(),
                formation.getModule(),
                formation.getTypeFormation(),
                formation.getFamilleFormation(),
                formation.getSousFamille(),
                formation.getReferenceFormation(),
                formation.getAnnee()
        );
    }
}
