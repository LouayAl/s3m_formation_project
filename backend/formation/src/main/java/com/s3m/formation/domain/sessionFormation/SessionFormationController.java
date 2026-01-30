package com.s3m.formation.domain.sessionFormation;

import com.s3m.formation.api.dto.SessionFormationResponseDto;
import com.s3m.formation.api.dto.UpdateSessionRequest;
import com.s3m.formation.domain.participation.AddParticipantsRequest;
import com.s3m.formation.domain.participation.ParticipationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionFormationController {

    private final SessionFormationService service;
    private final ParticipationService participationService;

    /* =========================
       READ
       ========================= */

    // Get all sessions
    @GetMapping
    public List<SessionFormationResponseDto> getAllSessions() {
        return service.getAllSessions();
    }

    // Get all sessions for a specific formation
    @GetMapping("/formations/{formationId}")
    public List<SessionFormationResponseDto> getByFormation(@PathVariable Integer formationId) {
        return service.getSessionsByFormation(formationId);
    }

    // Get single session by ID
    @GetMapping("/{sessionId}")
    public SessionFormationResponseDto getSession(@PathVariable Integer sessionId) {
        return service.getSession(sessionId);
    }

    /* =========================
       CREATE
       ========================= */

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SessionFormationResponseDto createSession(@Valid @RequestBody CreateSessionRequest request) {
        return service.toDto(service.createSession(request));
    }

    /* =========================
       UPDATE
       ========================= */

    @PutMapping("/{sessionId}")
    public SessionFormationResponseDto updateSession(@PathVariable Integer sessionId,
                                                     @RequestBody UpdateSessionRequest request) {
        return service.updateSession(sessionId, request);
    }


    /* =========================
       DELETE
       ========================= */

    @DeleteMapping("/{sessionId}")
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

    /* =========================
       PARTICIPANTS
       ========================= */

//    @PostMapping("/{sessionId}/participants")
//    public void addParticipants(
//            @PathVariable Integer sessionId,
//            @RequestBody AddParticipantsRequest request
//    ) {
//        participationService.addParticipants(sessionId, request.getEmployeIds());
//    }
}
