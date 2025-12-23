package com.s3m.formation.domain.reservation;


import com.s3m.formation.api.dto.DemandeReservationResponse;
import com.s3m.formation.security.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/demandes-reservation")
@RequiredArgsConstructor
public class DemandeReservationController {

    private final DemandeReservationService service;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DemandeReservationResponse> create(
            @RequestBody @Valid DemandeReservationRequest request
    ) {
        Integer entrepriseId = SecurityUtils.getEntrepriseId();
        if (entrepriseId == null) {
            throw new RuntimeException("Entreprise ID not found in security context");
        }

        return ResponseEntity.ok(
                service.create(request, entrepriseId)
        );
    }
}
