package com.s3m.formation.domain.sessionFormation;


import com.s3m.formation.api.dto.SessionResponseDto;
import com.s3m.formation.domain.participation.AddParticipantsRequest;
import com.s3m.formation.domain.participation.ParticipationService;
import lombok.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SessionFormationController {

    private final SessionFormationService service;
    private final ParticipationService participationService;

    @GetMapping("/formations/{formationId}/sessions")
    public List<SessionResponseDto> getByFormation(@PathVariable Integer formationId) {
        return service.getSessionsByFormation(formationId);
    }

    @GetMapping("/sessions/{sessionId}")
    public SessionResponseDto getSession(@PathVariable Integer sessionId) {
        return service.getSession(sessionId);
    }

    @PostMapping("/sessions")
    public SessionResponseDto createSession(@RequestBody CreateSessionRequest request) {
        SessionFormation session = service.createSession(request);
        return service.toDto(session);
    }

    @PostMapping("/sessions/{sessionId}/participants")
    public void addParticipants(@PathVariable Integer sessionId,
                                @RequestBody AddParticipantsRequest request) {
        participationService.addParticipants(sessionId, request.getEmployeIds());
    }
}
