package com.s3m.formation.domain.sessionFormation;


import com.s3m.formation.api.dto.SessionResponseDto;
import lombok.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SessionFormationController {

    private final SessionFormationService service;

    @GetMapping("/formations/{formationId}/sessions")
    public List<SessionResponseDto> getByFormation(
            @PathVariable Integer formationId
    ) {
        return service.getSessionsByFormation(formationId);
    }

    @GetMapping("/sessions/{sessionId}")
    public SessionResponseDto getSession(
            @PathVariable Integer sessionId
    ) {
        return service.getSession(sessionId);
    }
}
