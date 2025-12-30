package com.s3m.formation.api.controller;

import com.s3m.formation.api.kpi.client.dto.ClientKpiResponse;
import com.s3m.formation.api.service.kpi.ClientKpiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clients/{clientId}/kpis")
@RequiredArgsConstructor
public class ClientKpiController {

    private final ClientKpiService clientKpiService;

    @GetMapping
    public ResponseEntity<ClientKpiResponse> getClientKpis(@PathVariable Integer clientId) {
        ClientKpiResponse response = clientKpiService.getClientKpis(clientId);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }
}
