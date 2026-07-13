package com.s3m.formation.domain.formation;

import com.s3m.formation.api.dto.FormationResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/formations")
@RequiredArgsConstructor
public class FormationController {

    private final FormationService formationService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','EQUIPMENT_MANAGER','TRAINER','VISITOR')")
    public List<FormationResponseDto> getAllFormations(
            @RequestParam(required = false) Integer entrepriseId // only used for ADMIN
    ) {
        return formationService.getVisibleFormationsForCurrentUser(entrepriseId);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','EQUIPMENT_MANAGER','TRAINER','VISITOR')")
    public FormationResponseDto getFormationById(@PathVariable Integer id) {
        return formationService.getFormationById(id);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','EQUIPMENT_MANAGER','TRAINER','VISITOR')")
    public List<FormationResponseDto> search(@RequestParam String keyword) {
        return formationService.searchFormations(keyword);
    }

    @GetMapping("/filter")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','EQUIPMENT_MANAGER','TRAINER','VISITOR')")
    public List<FormationResponseDto> filter(
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String famille,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String sousFamille
    ) {
        return formationService.filterFormations(module, famille, type, sousFamille);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','EQUIPMENT_MANAGER')")
    public FormationResponseDto create(@RequestBody Formation formation) {
        return formationService.createFormation(formation);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','EQUIPMENT_MANAGER')")
    public FormationResponseDto update(@PathVariable Integer id,
                                       @RequestBody Formation formation) {
        return formationService.updateFormation(id, formation);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','EQUIPMENT_MANAGER')")
    public void delete(@PathVariable Integer id) {
        formationService.deleteFormation(id);
    }
}