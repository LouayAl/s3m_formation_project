package com.s3m.formation.domain.participation;

import com.s3m.formation.api.dto.ParticipantResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions/{sessionId}/participants")
@RequiredArgsConstructor
public class ParticipationController {

    private final ParticipationService service;

    /* =========================
       READ
       ========================= */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','EQUIPMENT_MANAGER','TRAINER','VISITOR','ADMIN_FINANCE')")
    public List<ParticipantResponseDto> getParticipants(@PathVariable Integer sessionId) {
        return service.getParticipantsBySession(sessionId);
    }

    @GetMapping("/count")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','EQUIPMENT_MANAGER','TRAINER','VISITOR','ADMIN_FINANCE')")
    public long countParticipants(@PathVariable Integer sessionId) {
        return service.countParticipants(sessionId);
    }

    /* =========================
       ADD
       ========================= */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','EQUIPMENT_MANAGER','TRAINER','ADMIN_FINANCE')")
    public void addParticipants(@PathVariable Integer sessionId,
                                @RequestBody List<Integer> employeIds) {
        service.addParticipants(sessionId, employeIds);
    }

    /* =========================
   DELETE ONE PARTICIPANT
   ========================= */
    @DeleteMapping("/{employeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','EQUIPMENT_MANAGER','TRAINER','ADMIN_FINANCE')")
    public void deleteParticipant(
            @PathVariable Integer sessionId,
            @PathVariable Integer employeId
    ) {
        service.deleteParticipant(sessionId, employeId);
    }

    /* =========================
       DELETE
       ========================= */
    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','EQUIPMENT_MANAGER','TRAINER','ADMIN_FINANCE')")
    public void deleteParticipants(@PathVariable Integer sessionId,
                                   @RequestBody List<Integer> employeIds) {
        service.deleteParticipants(sessionId, employeIds);
    }

}
