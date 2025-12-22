package com.s3m.formation.domain.fiche;

import com.s3m.formation.api.dto.FicheTechniqueResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FicheTechniqueFormationService {


    private final FicheTechniqueFormationRepository repository;

    public List<FicheTechniqueResponseDto> getAllByFormation(Integer formationId) {
        return repository.findByFormation_IdFormation(formationId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public FicheTechniqueResponseDto getActiveByFormation(Integer formationId) {
        return repository
                .findByFormation_IdFormationAndStatut(formationId, "ACTIVE")
                .map(this::toDto)
                .orElse(null); // later → throw exception
    }

    private FicheTechniqueResponseDto toDto(FicheTechniqueFormation fiche) {
        return new FicheTechniqueResponseDto(
                fiche.getIdFiche(),
                fiche.getVersionNumero(),
                fiche.getStatut(),
                fiche.getDateCreation(),
                fiche.getDateActivation(),
                fiche.getDescription(),
                fiche.getObjectifs(),
                fiche.getCompetencesCible(),
                fiche.getPrerequis(),
                fiche.getPopulationCible(),
                fiche.getProgramme(),
                fiche.getDureeJours(),
                fiche.getNbParticipantsMin(),
                fiche.getNbParticipantsMax()
        );
    }
}
