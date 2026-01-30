package com.s3m.formation.domain.participation;


import com.s3m.formation.api.dto.ParticipantResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/{sessionId}/participants")
    @ResponseStatus(HttpStatus.CREATED)
    public void addParticipants(
            @PathVariable Integer sessionId,
            @RequestBody List<Integer> employeIds
    ) {
        service.addParticipants(sessionId, employeIds);
    }
}
