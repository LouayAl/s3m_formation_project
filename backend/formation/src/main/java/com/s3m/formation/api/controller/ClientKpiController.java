package com.s3m.formation.api.controller;

import com.s3m.formation.api.kpi.client.dto.ClientKpiResponse;
import com.s3m.formation.api.kpi.client.dto.TotalGrowthKpiDto;
import com.s3m.formation.api.service.kpi.ClientKpiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients/{clientId}/kpis")
@RequiredArgsConstructor
public class ClientKpiController {

    private final ClientKpiService clientKpiService;

    private Integer getAuthenticatedEntrepriseId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (Integer) auth.getDetails();
    }

    private boolean isUnauthorized(Integer clientId) {
        return !getAuthenticatedEntrepriseId().equals(clientId);
    }

    @GetMapping
    public ResponseEntity<ClientKpiResponse> getClientKpis(
            @PathVariable Integer clientId,
            @RequestParam(required = false) List<Integer> years
    ) {
        if (isUnauthorized(clientId)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        Integer[] yearsArray = (years == null || years.isEmpty())
                ? new Integer[0]
                : years.toArray(new Integer[0]);
        ClientKpiResponse response = clientKpiService.getClientKpis(clientId, yearsArray);
        return response != null ? ResponseEntity.ok(response) : ResponseEntity.notFound().build();
    }

    @GetMapping("/years")
    public ResponseEntity<List<Integer>> getAvailableYears(@PathVariable Integer clientId) {
        if (isUnauthorized(clientId)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        return ResponseEntity.ok(clientKpiService.getAvailableYears(clientId));
    }

    @GetMapping("/total-growth")
    public ResponseEntity<TotalGrowthKpiDto> getTotalGrowth(
            @PathVariable Integer clientId,
            @RequestParam(defaultValue = "monthly") String period,
            @RequestParam(required = false) String month,
            @RequestParam(required = false) List<Integer> years
    ) {
        if (isUnauthorized(clientId)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        Integer[] yearsArray = (years == null || years.isEmpty())
                ? new Integer[0]
                : years.toArray(new Integer[0]);
        return ResponseEntity.ok(clientKpiService.getTotalGrowthKpi(clientId, period, month, yearsArray));
    }
}