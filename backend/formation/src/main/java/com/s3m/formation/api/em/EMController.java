package com.s3m.formation.api.em;

import com.s3m.formation.api.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/em")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EMController {

    private final EMService emService;

    // GET /api/em/dashboard
    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','EQUIPMENT_MANAGER')")
    public EMDashboardKpiDto getDashboard() {
        return emService.getDashboardKpis();
    }

    // GET /api/em/sessions/{sessionId}/evaluations
    @GetMapping("/sessions/{sessionId}/evaluations")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','EQUIPMENT_MANAGER')")
    public List<EvaluationDto> getSessionEvaluations(@PathVariable Integer sessionId) {
        return emService.getEvaluationsForSession(sessionId);
    }

    // GET /api/em/sessions/{sessionId}/participants/{employeId}/evaluations
    @GetMapping("/sessions/{sessionId}/participants/{employeId}/evaluations")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','EQUIPMENT_MANAGER')")
    public List<EvaluationDto> getParticipantEvaluations(
            @PathVariable Integer sessionId,
            @PathVariable Integer employeId
    ) {
        return emService.getEvaluationsForParticipant(sessionId, employeId);
    }

    // GET /api/em/sessions/{sessionId}/stats
    @GetMapping("/sessions/{sessionId}/stats")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','EQUIPMENT_MANAGER')")
    public List<Map<String, Object>> getSessionStats(@PathVariable Integer sessionId) {
        return emService.getParticipantStats(sessionId);
    }

    // POST /api/em/evaluations  — create or update (upsert)
    @PostMapping("/evaluations")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','EQUIPMENT_MANAGER')")
    public EvaluationDto saveEvaluation(@RequestBody EvaluationRequest request) {
        return emService.saveEvaluation(request);
    }

    // GET /api/em/sessions/{sessionId}/days/{jour}/criteres
    @GetMapping("/sessions/{sessionId}/days/{jour}/criteres")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','EQUIPMENT_MANAGER')")
    public List<SessionCritereDto> getCriteres(
            @PathVariable Integer sessionId,
            @PathVariable Integer jour
    ) {
        return emService.getCriteres(sessionId, jour);
    }

    // POST /api/em/sessions/{sessionId}/days/{jour}/criteres
    @PostMapping("/sessions/{sessionId}/days/{jour}/criteres")
    @PreAuthorize("hasAnyAuthority('ADMIN','EQUIPMENT_MANAGER')")
    public List<SessionCritereDto> saveCriteres(
            @PathVariable Integer sessionId,
            @PathVariable Integer jour,
            @RequestBody SessionCritereRequest request
    ) {
        return emService.saveCriteres(sessionId, jour, request);
    }

    // GET /api/em/sessions
    @GetMapping("/sessions")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','EQUIPMENT_MANAGER')")
    public List<SessionFormationResponseDto> getSessionsForCurrentUser() {
        return emService.getSessionsForCurrentUser();
    }

    // GET /api/em/sessions/{sessionId}
    @GetMapping("/sessions/{sessionId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','EQUIPMENT_MANAGER')")
    public SessionFormationResponseDto getSessionForCurrentUser(@PathVariable Integer sessionId) {
        return emService.getSessionForCurrentUser(sessionId);
    }

    @GetMapping("/employes")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','EQUIPMENT_MANAGER')")
    public List<EmployeResponseDto> getEmployesForCurrentUser() {
        return emService.getEmployesForCurrentUser();
    }

}