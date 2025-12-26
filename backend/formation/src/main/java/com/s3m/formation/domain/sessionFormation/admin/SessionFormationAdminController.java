package com.s3m.formation.domain.sessionFormation.admin;


import com.s3m.formation.api.dto.SessionFormationAuditDto;
import com.s3m.formation.domain.sessionFormation.SessionFormationService;
import com.s3m.formation.domain.sessionFormation.SessionFormationStatut;
import com.s3m.formation.domain.sessionFormation.admin.dto.SessionFormationAdminListDto;
import com.s3m.formation.domain.sessionFormation.admin.dto.SessionFormationAdminUpdateRequest;
import com.s3m.formation.domain.sessionFormation.sessionFormationAudit.SessionFormationAuditService;
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
    private final SessionFormationAdminService serviceAdmin;
    private final SessionFormationService service;
    private final SessionFormationAuditService auditService;


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
        serviceAdmin.updateSession(id, request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{sessionId}/demarrer")
    public ResponseEntity<Void> demarrer(@PathVariable Integer sessionId) {
        service.demarrerSession(sessionId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{sessionId}/terminer")
    public ResponseEntity<Void> terminer(@PathVariable Integer sessionId) {
        service.terminerSession(sessionId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{sessionId}/annuler")
    public ResponseEntity<Void> annuler(@PathVariable Integer sessionId) {
        service.annulerSession(sessionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{sessionId}/audit")
    public List<SessionFormationAuditDto> audit(
            @PathVariable Integer sessionId
    ) {
        return auditService.getAuditBySession(sessionId);
    }
}
