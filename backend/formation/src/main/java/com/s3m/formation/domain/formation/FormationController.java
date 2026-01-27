package com.s3m.formation.domain.formation;

import com.s3m.formation.api.dto.FormationResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/formations")
@RequiredArgsConstructor
public class FormationController {

    private final FormationService formationService;

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
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String famille,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String sousFamille
    ) {
        return formationService.filterFormations(module, famille, type, sousFamille);
    }

    @PostMapping
    public FormationResponseDto create(@RequestBody Formation formation) {
        return formationService.createFormation(formation);
    }

    @PutMapping("/{id}")
    public FormationResponseDto update(@PathVariable Integer id, @RequestBody Formation formation) {
        return formationService.updateFormation(id, formation);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        formationService.deleteFormation(id);
    }
}
