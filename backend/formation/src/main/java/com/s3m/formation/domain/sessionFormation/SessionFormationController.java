package com.s3m.formation.domain.sessionFormation;

import com.s3m.formation.api.dto.SessionFormationResponseDto;
import com.s3m.formation.api.dto.UpdateSessionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionFormationController {

    private final SessionFormationService service;

    /* =========================
       READ
       ========================= */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','EQUIPMENT_MANAGER','TRAINER','VISITOR','ADMIN_FINANCE')")
    public List<SessionFormationResponseDto> getAllSessions(
            @RequestParam(required = false) Integer entrepriseId
    ) {
        return service.getAllSessions(entrepriseId);
    }

    // =========================
    // GET PAGINATED
    // Usage: GET /api/sessions/paginated?page=0&size=20&search=java&entrepriseId=4&years=2025,2026&statuts=EN_COURS,TERMINEE&facture=false
    // =========================
    @GetMapping("/paginated")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','EQUIPMENT_MANAGER','TRAINER','VISITOR','ADMIN_FINANCE')")
    public Page<SessionFormationResponseDto> getSessionsPaginated(
            @RequestParam(required = false)              Integer entrepriseId, // only used for ADMIN / ADMIN_FINANCE
            @RequestParam(required = false)              String search,
            @RequestParam(required = false)              String years,         // comma-separated, e.g. "2025,2026"
            @RequestParam(required = false)              String statuts,       // comma-separated, e.g. "EN_COURS,TERMINEE" — finance view
            @RequestParam(required = false)              Boolean facture,      // finance view only — ignored server-side for everyone else
            @RequestParam(defaultValue = "0")            int page,
            @RequestParam(defaultValue = "20")           int size,
            @RequestParam(defaultValue = "idSession")    String sortBy,
            @RequestParam(defaultValue = "desc")         String sortDir
    ) {
        List<Integer> yearsList = (years == null || years.isBlank())
                ? null
                : Arrays.stream(years.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .toList();

        List<SessionFormationStatut> statutsList = (statuts == null || statuts.isBlank())
                ? null
                : Arrays.stream(statuts.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(SessionFormationStatut::valueOf)
                .toList();

        return service.getSessionsPaginated(entrepriseId, search, yearsList, statutsList, facture, page, size, sortBy, sortDir);
    }

    // =========================
    // GET AVAILABLE YEARS (for the year-filter dropdown)
    // =========================
    @GetMapping("/years")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','EQUIPMENT_MANAGER','TRAINER','VISITOR','ADMIN_FINANCE')")
    public List<Integer> getAvailableYears(
            @RequestParam(required = false) Integer entrepriseId // only used for ADMIN / ADMIN_FINANCE
    ) {
        return service.getAvailableYears(entrepriseId);
    }

    @GetMapping("/formations/{formationId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','VISITOR','ADMIN_FINANCE')")
    public List<SessionFormationResponseDto> getByFormation(@PathVariable Integer formationId) {
        return service.getSessionsByFormation(formationId);
    }

    @GetMapping("/{sessionId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','EQUIPMENT_MANAGER','TRAINER','VISITOR','ADMIN_FINANCE')")
    public SessionFormationResponseDto getSession(@PathVariable Integer sessionId) {
        return service.getSession(sessionId);
    }

    /* =========================
       CREATE
       (ADMIN_FINANCE intentionally excluded — read-only + facture toggle only)
       ========================= */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','EQUIPMENT_MANAGER')")
    public SessionFormationResponseDto createSession(@RequestBody CreateSessionRequest request) {
        return service.toDto(service.createSession(request));
    }

    /* =========================
       UPDATE
       (ADMIN_FINANCE intentionally excluded)
       ========================= */
    @PutMapping("/{sessionId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','EQUIPMENT_MANAGER','TRAINER','ADMIN_FINANCE')")
    public SessionFormationResponseDto updateSession(@PathVariable Integer sessionId,
                                                     @RequestBody UpdateSessionRequest request) {
        return service.updateSession(sessionId, request);
    }

    /* =========================
       DELETE
       (ADMIN_FINANCE intentionally excluded)
       ========================= */
    @DeleteMapping("/{sessionId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','EQUIPMENT_MANAGER')")
    public void deleteSession(@PathVariable Integer sessionId) {
        service.deleteSession(sessionId);
    }

    /* =========================
       TRANSITIONS
       (ADMIN_FINANCE intentionally excluded — not part of "read-only + facture toggle")
       ========================= */
    @PostMapping("/{sessionId}/demarrer")
    public void demarrerSession(@PathVariable Integer sessionId) {
        service.demarrerSession(sessionId);
    }

    @PostMapping("/{sessionId}/terminer")
    public void terminerSession(@PathVariable Integer sessionId) {
        service.terminerSession(sessionId);
    }

    @PostMapping("/{sessionId}/annuler")
    public void annulerSession(@PathVariable Integer sessionId) {
        service.annulerSession(sessionId);
    }

    @PutMapping("/{sessionId}/participants")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','EQUIPMENT_MANAGER','TRAINER')")
    public void updateParticipants(@PathVariable Integer sessionId,
                                   @RequestBody List<Integer> participantIds) {
        service.updateParticipants(sessionId, participantIds);
    }

    /* =========================
       FACTURATION — ADMIN_FINANCE only
       ========================= */
    @PatchMapping("/{sessionId}/facture")
    @PreAuthorize("hasAuthority('ADMIN_FINANCE')")
    public SessionFormationResponseDto toggleFacture(@PathVariable Integer sessionId) {
        return service.toggleFacture(sessionId);
    }

}