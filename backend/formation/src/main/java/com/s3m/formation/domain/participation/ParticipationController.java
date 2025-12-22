package com.s3m.formation.domain.participation;


import com.s3m.formation.api.dto.ParticipantResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class ParticipationController {

    private final ParticipationService service;

    @GetMapping("/{sessionId}/participants")
    public List<ParticipantResponseDto> getParticipants(
            @PathVariable Integer sessionId
    ) {
        return service.getParticipantsBySession(sessionId);
    }

    @GetMapping("/{sessionId}/participants/count")
    public long countParticipants(
            @PathVariable Integer sessionId
    ) {
        return service.countParticipants(sessionId);
    }

}
