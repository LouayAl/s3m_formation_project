// src/main/java/com/s3m/formation/domain/planification/PlanAnnuelController.java
package com.s3m.formation.domain.planification;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/planification")
@RequiredArgsConstructor
public class PlanAnnuelController {

    private final PlanAnnuelService service;

    /**
     * GET /api/planification/{annee}?entrepriseId=4
     * Returns planned targets + actual TERMINEE counts for that year/company.
     * Accessible to ADMIN and MANAGER (read-only for MANAGER).
     */
    @GetMapping("/{annee}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public PlanAnnuelDto getPlan(
            @PathVariable Integer annee,
            @RequestParam Integer entrepriseId
    ) {
        return service.getPlan(annee, entrepriseId);
    }

    /**
     * POST /api/planification
     * Upserts the monthly targets for a year/company.
     * ADMIN only.
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public PlanAnnuelDto savePlan(@RequestBody PlanAnnuelRequest request) {
        return service.savePlan(request);
    }
}