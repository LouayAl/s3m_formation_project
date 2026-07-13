package com.s3m.formation.domain.besoinFormation;

import com.s3m.formation.api.dto.BesoinFormationRequest;
import com.s3m.formation.api.dto.BesoinFormationResponseDto;
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
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','EQUIPMENT_MANAGER','TRAINER','VISITOR')")
    public List<BesoinFormationResponseDto> getAll(
            @RequestParam(required = false) Integer entrepriseId
    ) {
        return besoinFormationService.getVisibleBesoinsForCurrentUser(entrepriseId);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','EQUIPMENT_MANAGER','TRAINER','VISITOR')")
    public BesoinFormationResponseDto getById(@PathVariable Integer id) {
        return besoinFormationService.getBesoinById(id);
    }

    // =========================
    // CREATE / UPDATE / DELETE — ADMIN only
    // =========================
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ADMIN')")
    public BesoinFormationResponseDto create(@RequestBody BesoinFormationRequest request) {
        return besoinFormationService.createBesoin(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public BesoinFormationResponseDto update(@PathVariable Integer id, @RequestBody BesoinFormationRequest request) {
        return besoinFormationService.updateBesoin(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public void delete(@PathVariable Integer id) {
        besoinFormationService.deleteBesoin(id);
    }
}