package com.s3m.formation.domain.besoinFormation;

import com.s3m.formation.api.dto.BesoinFormationRequest;
import com.s3m.formation.api.dto.BesoinFormationResponseDto;
import com.s3m.formation.api.dto.BesoinDecisionRequest;
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
    // Create: admins may add approved catalogue rows; department heads may submit requests.
    // =========================
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ADMIN','CHEF_DEPARTEMENT')")
    public BesoinFormationResponseDto create(@RequestBody BesoinFormationRequest request) {
        return besoinFormationService.createBesoin(request);
    }

    @PatchMapping("/{id}/decision")
    @PreAuthorize("hasAuthority('ADMIN')")
    public BesoinFormationResponseDto decide(@PathVariable Integer id, @RequestBody BesoinDecisionRequest request) {
        return besoinFormationService.decideBesoin(id, request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','ADMIN_FINANCE')")
    public BesoinFormationResponseDto update(@PathVariable Integer id, @RequestBody BesoinFormationRequest request) {
        return besoinFormationService.updateBesoin(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','ADMIN_FINANCE')")
    public void delete(@PathVariable Integer id) {
        besoinFormationService.deleteBesoin(id);
    }
}
