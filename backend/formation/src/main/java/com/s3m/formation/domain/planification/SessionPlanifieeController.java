package com.s3m.formation.domain.planification;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/planification")
public class SessionPlanifieeController {

    private final SessionPlanifieeService service;

    public SessionPlanifieeController(SessionPlanifieeService service) {
        this.service = service;
    }

    /**
     * GET /api/planification/{annee}?entrepriseId=3
     * Returns all planned sessions for the year + monthly aggregations.
     */
    @GetMapping("/{annee}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','EQUIPMENT_MANAGER','VISITOR','ADMIN_FINANCE')")
    public ResponseEntity<PlanificationAnnuelleResponse> getYear(
            @PathVariable int annee,
            @RequestParam Integer entrepriseId
    ) {
        return ResponseEntity.ok(service.getYear(annee, entrepriseId));
    }

    /**
     * POST /api/planification/bulk
     * Bulk-add N planned sessions on a date.
     * Body: { entrepriseId, dateSession, count, dHeures, notes }
     */
    @PostMapping("/bulk")
    @PreAuthorize("hasAuthority('ADMIN','ADMIN_FINANCE')")
    public ResponseEntity<PlanificationAnnuelleResponse> bulkAdd(
            @RequestBody BulkAddRequest req
    ) {
        return ResponseEntity.ok(service.bulkAdd(req));
    }

    /**
     * PUT /api/planification/sessions/{id}?entrepriseId=3
     * Update a single planned session.
     */
    @PutMapping("/sessions/{id}")
    @PreAuthorize("hasAuthority('ADMIN','ADMIN_FINANCE')")
    public ResponseEntity<PlanificationAnnuelleResponse> update(
            @PathVariable Integer id,
            @RequestParam Integer entrepriseId,
            @RequestBody SessionPlanifieeUpdateRequest req
    ) {
        return ResponseEntity.ok(service.update(id, entrepriseId, req));
    }

    /**
     * DELETE /api/planification/sessions/{id}?entrepriseId=3
     * Delete a single planned session.
     */
    @DeleteMapping("/sessions/{id}")
    @PreAuthorize("hasAuthority('ADMIN','ADMIN_FINANCE')")
    public ResponseEntity<Void> delete(
            @PathVariable Integer id,
            @RequestParam Integer entrepriseId
    ) {
        service.delete(id, entrepriseId);
        return ResponseEntity.noContent().build();
    }
}