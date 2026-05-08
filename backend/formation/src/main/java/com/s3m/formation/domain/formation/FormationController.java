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
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','EQUIPMENT_MANAGER','TRAINER')")
    public List<FormationResponseDto> getAllFormations() {
        return formationService.getVisibleFormationsForCurrentUser();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public FormationResponseDto getFormationById(@PathVariable Integer id) {
        return formationService.getFormationById(id);
    }

    @GetMapping("/reference/{reference}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public FormationResponseDto getByReference(@PathVariable String reference) {
        return formationService.getFormationByReference(reference);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public List<FormationResponseDto> search(@RequestParam String keyword) {
        return formationService.searchFormations(keyword);
    }

    @GetMapping("/filter")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
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
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public FormationResponseDto update(@PathVariable Integer id, @RequestBody Formation formation) {
        return formationService.updateFormation(id, formation);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public void delete(@PathVariable Integer id) {
        formationService.deleteFormation(id);
    }

    @PostMapping("/import")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<String> importExcel(@RequestParam("file") MultipartFile file) {

        String message = formationService.importFromExcel(file);

        return ResponseEntity.ok(message);
    }


}
