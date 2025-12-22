package com.s3m.formation.domain.formation;

import com.s3m.formation.api.dto.FormationResponseDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/formations")
public class FormationController {

    private final FormationService formationService;

    public FormationController(FormationService formationService) {
        this.formationService = formationService;
    }

    @GetMapping
    public List<FormationResponseDto> getAllFormations() {
        return formationService.getAllFormations();
    }

    @GetMapping("/{id}")
    public FormationResponseDto getFormationById(@PathVariable Integer id) {
        return formationService.getFormationById(id);
    }

    @GetMapping("/reference/{reference}")
    public FormationResponseDto getByReference(@PathVariable String reference) {
        return formationService.getFormationByReference(reference);
    }

    @GetMapping("/search")
    public List<FormationResponseDto> search(@RequestParam String keyword) {
        return formationService.searchFormations(keyword);
    }

    @GetMapping("/filter")
    public List<FormationResponseDto> filter(
            @RequestParam(required = false) String familleFormation,
            @RequestParam(required = false) String typeFormation,
            @RequestParam(required = false) Integer annee
    ) {
        return formationService.filterFormations(
                familleFormation,
                typeFormation,
                annee
        );
    }

}
