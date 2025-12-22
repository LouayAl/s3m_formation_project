package com.s3m.formation.domain.fiche;


import com.s3m.formation.api.dto.FicheTechniqueResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/formations/{formationId}/fiches-techniques")
@RequiredArgsConstructor
public class FicheTechniqueFormationController {
    private final FicheTechniqueFormationService service;

    @GetMapping
    public List<FicheTechniqueResponseDto> getAll(
            @PathVariable Integer formationId
    ) {
        return service.getAllByFormation(formationId);
    }

    @GetMapping("/active")
    public FicheTechniqueResponseDto getActive(
            @PathVariable Integer formationId
    ) {
        return service.getActiveByFormation(formationId);
    }
}
