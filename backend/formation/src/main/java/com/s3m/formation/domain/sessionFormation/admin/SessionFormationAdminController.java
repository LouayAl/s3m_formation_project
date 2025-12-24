package com.s3m.formation.domain.sessionFormation.admin;


import com.s3m.formation.domain.sessionFormation.SessionFormationStatut;
import com.s3m.formation.domain.sessionFormation.admin.dto.SessionFormationAdminListDto;
import com.s3m.formation.domain.sessionFormation.admin.dto.SessionFormationAdminUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/sessions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class SessionFormationAdminController {

    private final SessionFormationAdminQueryService queryService;
    private final SessionFormationAdminService service;

    @GetMapping
    public List<SessionFormationAdminListDto> search(
            @RequestParam(required = false) SessionFormationStatut statut,
            @RequestParam(required = false) Integer formationId,
            @RequestParam(required = false) Integer entrepriseId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return queryService.search(
                statut,
                formationId,
                entrepriseId,
                startDate,
                endDate
        );
    }


    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateSession(
            @PathVariable Integer id,
            @RequestBody SessionFormationAdminUpdateRequest request
    ) {
        service.updateSession(id, request);
        return ResponseEntity.noContent().build();
    }
}
