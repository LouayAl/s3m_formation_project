package com.s3m.formation.domain.besoinFormation;

import com.s3m.formation.api.dto.BesoinFormationRequest;
import com.s3m.formation.api.dto.BesoinFormationResponseDto;
import com.s3m.formation.api.dto.BesoinDecisionRequest;
import com.s3m.formation.api.dto.BulkSubmitBesoinsRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/besoins-formation")
@RequiredArgsConstructor
public class BesoinFormationController {

    private final BesoinFormationService besoinFormationService;

    // =========================
    // GET ALL — everyone can view (scoped to their own entreprise);
    // entrepriseId param only has effect for ADMIN.
    // =========================
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','CHEF_DEPARTEMENT','EQUIPMENT_MANAGER','TRAINER','VISITOR','ADMIN_FINANCE')")
    public List<BesoinFormationResponseDto> getAll(
            @RequestParam(required = false) Integer entrepriseId
    ) {
        return besoinFormationService.getVisibleBesoinsForCurrentUser(entrepriseId);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','CHEF_DEPARTEMENT','EQUIPMENT_MANAGER','TRAINER','VISITOR','ADMIN_FINANCE')")
    public BesoinFormationResponseDto getById(@PathVariable Integer id) {
        return besoinFormationService.getBesoinById(id);
    }

    // =========================
    // Admins and department scoped requesters may create training needs.
    // =========================
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','CHEF_DEPARTEMENT')")
    public BesoinFormationResponseDto create(@RequestBody BesoinFormationRequest request) {
        return besoinFormationService.createBesoin(request);
    }

    @PatchMapping("/{id}/decision")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public BesoinFormationResponseDto decide(@PathVariable Integer id, @RequestBody BesoinDecisionRequest request) {
        return besoinFormationService.decideBesoin(id, request);
    }

    @PatchMapping("/{id}/submit")
    @PreAuthorize("hasAuthority('CHEF_DEPARTEMENT')")
    public BesoinFormationResponseDto submitDraft(@PathVariable Integer id) {
        return besoinFormationService.submitDraft(id);
    }

    @PatchMapping("/submit/bulk")
    @PreAuthorize("hasAuthority('CHEF_DEPARTEMENT')")
    public List<BesoinFormationResponseDto> submitDrafts(@RequestBody BulkSubmitBesoinsRequest request) {
        return besoinFormationService.submitDrafts(request.ids());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','ADMIN_FINANCE','CHEF_DEPARTEMENT','MANAGER')")
    public BesoinFormationResponseDto update(@PathVariable Integer id, @RequestBody BesoinFormationRequest request) {
        return besoinFormationService.updateBesoin(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','ADMIN_FINANCE','MANAGER')")
    public void delete(@PathVariable Integer id) {
        besoinFormationService.deleteBesoin(id);
    }
}
