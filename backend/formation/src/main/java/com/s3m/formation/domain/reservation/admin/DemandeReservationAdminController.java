package com.s3m.formation.domain.reservation.admin;

import com.s3m.formation.api.dto.PlanifierDemandeRequest;
import com.s3m.formation.domain.reservation.DemandeReservation;
import com.s3m.formation.domain.reservation.DemandeReservationStatut;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/demandes-reservation")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class DemandeReservationAdminController {

    private final DemandeReservationAdminService service;
    private final DemandeReservationAdminQueryService queryService;

    @PatchMapping("/{id}/valider")
    public ResponseEntity<Void> validate(@PathVariable Integer id) {
        service.validateDemande(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/refuser")
    public ResponseEntity<Void> refuse(
            @PathVariable Integer id,
            @RequestBody String reason
    ) {
        service.refuseDemande(id, reason);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/annuler")
    public ResponseEntity<Void> cancel(@PathVariable Integer id) {
        service.cancelDemande(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/planifier")
    public ResponseEntity<Void> planifier(
            @PathVariable Integer id,
            @RequestBody PlanifierDemandeRequest request
    ) {
        service.planifier(id, request);
        return ResponseEntity.noContent().build();
    }
    @GetMapping
    public List<DemandeReservation> getAll(
            @RequestParam(required = false) DemandeReservationStatut statut
    ) {
        if (statut != null) {
            return queryService.findByStatut(statut);
        }
        return queryService.findAll();
    }

    @GetMapping("/{id}")
    public DemandeReservation getOne(@PathVariable Integer id) {
        return queryService.findOne(id);
    }
}
