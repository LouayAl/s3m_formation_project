package com.s3m.formation.api.controller;

import com.s3m.formation.api.kpi.client.dto.ClientKpiResponse;
import com.s3m.formation.api.kpi.client.dto.TotalGrowthKpiDto;
import com.s3m.formation.api.kpi.client.dto.VisibiliteKpiDto;
import com.s3m.formation.api.kpi.client.dto.VisibiliteSessionDto;
import com.s3m.formation.api.service.kpi.ClientKpiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ClientKpiController {

    private final ClientKpiService clientKpiService;

    private Integer getAuthenticatedEntrepriseId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (Integer) auth.getDetails();
    }

    private boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(authority -> "ADMIN".equals(authority.getAuthority()));
    }

    private boolean isUnauthorized(Integer clientId) {
        return !isAdmin() && !getAuthenticatedEntrepriseId().equals(clientId);
    }

    private Integer[] toYearsArray(List<Integer> years) {
        return (years == null || years.isEmpty())
                ? new Integer[0]
                : years.toArray(new Integer[0]);
    }

    @GetMapping("/clients/{clientId}/kpis")
    public ResponseEntity<ClientKpiResponse> getClientKpis(
            @PathVariable Integer clientId,
            @RequestParam(required = false) List<Integer> years
    ) {
        if (isUnauthorized(clientId)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        ClientKpiResponse response = clientKpiService.getClientKpis(clientId, toYearsArray(years));
        return response != null ? ResponseEntity.ok(response) : ResponseEntity.notFound().build();
    }

    @GetMapping("/clients/{clientId}/kpis/years")
    public ResponseEntity<List<Integer>> getAvailableYears(@PathVariable Integer clientId) {
        if (isUnauthorized(clientId)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        return ResponseEntity.ok(clientKpiService.getAvailableYears(clientId));
    }

    @GetMapping("/clients/{clientId}/kpis/total-growth")
    public ResponseEntity<TotalGrowthKpiDto> getTotalGrowth(
            @PathVariable Integer clientId,
            @RequestParam(defaultValue = "monthly") String period,
            @RequestParam(required = false) String month,
            @RequestParam(required = false) List<Integer> years
    ) {
        if (isUnauthorized(clientId)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        return ResponseEntity.ok(clientKpiService.getTotalGrowthKpi(clientId, period, month, toYearsArray(years)));
    }

    @GetMapping("/admin/kpis")
    public ResponseEntity<ClientKpiResponse> getAdminKpis(
            @RequestParam(required = false) List<Integer> years
    ) {
        if (!isAdmin()) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        return ResponseEntity.ok(clientKpiService.getClientKpis(null, toYearsArray(years)));
    }

    @GetMapping("/admin/kpis/years")
    public ResponseEntity<List<Integer>> getAdminAvailableYears() {
        if (!isAdmin()) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        return ResponseEntity.ok(clientKpiService.getAvailableYears(null));
    }

    @GetMapping("/admin/kpis/total-growth")
    public ResponseEntity<TotalGrowthKpiDto> getAdminTotalGrowth(
            @RequestParam(defaultValue = "monthly") String period,
            @RequestParam(required = false) String month,
            @RequestParam(required = false) List<Integer> years
    ) {
        if (!isAdmin()) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        return ResponseEntity.ok(clientKpiService.getTotalGrowthKpi(null, period, month, toYearsArray(years)));
    }


    @GetMapping("/clients/{clientId}/kpis/visibilite")
    public ResponseEntity<VisibiliteKpiDto> getVisibiliteKpis(
            @PathVariable Integer clientId,
            @RequestParam String start,
            @RequestParam String end) {
        if (isUnauthorized(clientId)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        return ResponseEntity.ok(
                clientKpiService.getVisibiliteKpis(clientId, LocalDate.parse(start), LocalDate.parse(end)));
    }

    @GetMapping("/clients/{clientId}/kpis/visibilite/sessions")
    public ResponseEntity<List<VisibiliteSessionDto>> getVisibiliteSessions(
            @PathVariable Integer clientId,
            @RequestParam String start,
            @RequestParam String end) {
        if (isUnauthorized(clientId)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        return ResponseEntity.ok(
                clientKpiService.getVisibiliteSessions(clientId, LocalDate.parse(start), LocalDate.parse(end)));
    }

    @GetMapping("/admin/kpis/visibilite")
    public ResponseEntity<VisibiliteKpiDto> getAdminVisibiliteKpis(
            @RequestParam String start,
            @RequestParam String end) {
        if (!isAdmin()) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        return ResponseEntity.ok(
                clientKpiService.getVisibiliteKpis(null, LocalDate.parse(start), LocalDate.parse(end)));
    }

    @GetMapping("/admin/kpis/visibilite/sessions")
    public ResponseEntity<List<VisibiliteSessionDto>> getAdminVisibiliteSessions(
            @RequestParam String start,
            @RequestParam String end) {
        if (!isAdmin()) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        return ResponseEntity.ok(
                clientKpiService.getVisibiliteSessions(null, LocalDate.parse(start), LocalDate.parse(end)));
    }


}
