package com.s3m.formation.domain.sessionFormation;

import com.s3m.formation.api.dto.SessionFormationResponseDto;
import com.s3m.formation.api.dto.UpdateSessionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public List<SessionFormationResponseDto> getAllSessions() {
        return service.getAllSessions();
    }

    @GetMapping("/formations/{formationId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public List<SessionFormationResponseDto> getByFormation(@PathVariable Integer formationId) {
        return service.getSessionsByFormation(formationId);
    }

    @GetMapping("/{sessionId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public SessionFormationResponseDto getSession(@PathVariable Integer sessionId) {
        return service.getSession(sessionId);
    }

    /* =========================
       CREATE
       ========================= */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public SessionFormationResponseDto createSession(@RequestBody CreateSessionRequest request) {
        return service.toDto(service.createSession(request));
    }

    /* =========================
       UPDATE
       ========================= */
    @PutMapping("/{sessionId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public SessionFormationResponseDto updateSession(@PathVariable Integer sessionId,
                                                     @RequestBody UpdateSessionRequest request) {
        return service.updateSession(sessionId, request);
    }

    /* =========================
       DELETE
       ========================= */
    @DeleteMapping("/{sessionId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteSession(@PathVariable Integer sessionId) {
        service.deleteSession(sessionId);
    }

    /* =========================
       TRANSITIONS
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
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public void updateParticipants(@PathVariable Integer sessionId,
                                   @RequestBody List<Integer> participantIds) {
        service.updateParticipants(sessionId, participantIds);
    }

}
