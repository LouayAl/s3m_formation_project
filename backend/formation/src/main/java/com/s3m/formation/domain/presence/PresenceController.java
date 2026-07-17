package com.s3m.formation.domain.presence;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/sessions/{sessionId}/presence")
public class PresenceController {

    private final PresenceService service;

    public PresenceController(PresenceService service) {
        this.service = service;
    }

    /**
     * GET /api/sessions/{sessionId}/presence/days
     * Returns all calendar days between dateDebut and dateFin for the session.
     */
    @GetMapping("/days")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','EQUIPMENT_MANAGER','TRAINER','VISITOR','ADMIN_FINANCE')")
    public ResponseEntity<List<LocalDate>> getSessionDays(@PathVariable Integer sessionId) {
        return ResponseEntity.ok(service.getSessionDays(sessionId));
    }

    /**
     * GET /api/sessions/{sessionId}/presence/recorded
     * Returns dates that already have presence records saved.
     */
    @GetMapping("/recorded")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','EQUIPMENT_MANAGER','TRAINER','VISITOR','ADMIN_FINANCE')")
    public ResponseEntity<PresenceDaysResponse> getRecordedDays(@PathVariable Integer sessionId) {
        return ResponseEntity.ok(service.getRecordedDays(sessionId));
    }

    /**
     * GET /api/sessions/{sessionId}/presence?jour=2025-01-15
     * Returns all participants with their presence status for a given day.
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','EQUIPMENT_MANAGER','TRAINER','VISITOR','ADMIN_FINANCE')")
    public ResponseEntity<PresenceJourResponse> getPresenceForDay(
            @PathVariable Integer sessionId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate jour
    ) {
        return ResponseEntity.ok(service.getPresenceForDay(sessionId, jour));
    }

    /**
     * POST /api/sessions/{sessionId}/presence
     * Save (upsert) presence for all participants on a given day.
     * Body: { jour: "2025-01-15", presences: [{ participationId, present }] }
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','EQUIPMENT_MANAGER','TRAINER','ADMIN_FINANCE')")
    public ResponseEntity<PresenceJourResponse> savePresence(
            @PathVariable Integer sessionId,
            @RequestBody SavePresenceRequest req
    ) {
        return ResponseEntity.ok(service.savePresence(sessionId, req));
    }
}