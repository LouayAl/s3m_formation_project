package com.s3m.formation.domain.evaluationAChaud;

import com.s3m.formation.api.dto.EvaluationAChaudRequest;
import com.s3m.formation.api.dto.EvaluationAChaudStatsDto;
import com.s3m.formation.api.dto.EvaluationSummaryDto;
import com.s3m.formation.api.dto.SatisfactionKpiDto;
import com.s3m.formation.domain.employe.Employe;
import com.s3m.formation.domain.participation.ParticipationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class EvaluationAChaudController {

    private final EvaluationAChaudService service;
    private final ParticipationRepository participationRepository;

    // ── PUBLIC (no JWT) ──────────────────────────────────────────────────────

    /** Returns participant list for the dropdown on the public form */
    @GetMapping("/api/public/sessions/{sessionId}/participants")
    public ResponseEntity<List<ParticipantDropdownItem>> getParticipants(
            @PathVariable Integer sessionId) {
        List<ParticipantDropdownItem> items = participationRepository
                .findBySession_IdSession(sessionId)
                .stream()
                .map(p -> {
                    Employe e = p.getEmploye();
                    return new ParticipantDropdownItem(
                            e.getIdEmploye(),
                            e.getNom() + " " + e.getPrenom()
                    );
                })
                .toList();
        return ResponseEntity.ok(items);
    }

    /** Public form submission */
    @PostMapping("/api/public/evaluation-a-chaud")
    public ResponseEntity<Void> submit(@RequestBody EvaluationAChaudRequest request) {
        service.submit(request);
        return ResponseEntity.ok().build();
    }

    // ── PROTECTED ────────────────────────────────────────────────────────────

    @GetMapping("/api/evaluation-a-chaud/session/{sessionId}/stats")
    public ResponseEntity<EvaluationAChaudStatsDto> getStats(
            @PathVariable Integer sessionId) {
        return ResponseEntity.ok(service.getStats(sessionId));
    }

    public record ParticipantDropdownItem(Integer idEmploye, String nomComplet) {}

    @GetMapping("/api/evaluation-a-chaud/summary")
    public ResponseEntity<List<EvaluationSummaryDto>> getSummary(
            org.springframework.security.core.Authentication auth) {
        Integer entrepriseId = (Integer) auth.getDetails();
        return ResponseEntity.ok(service.getSummaryForEntreprise(entrepriseId));
    }

    // Returns the hardcoded formulaire questions (used by public form)
    @GetMapping("/api/public/formulaire")
    public ResponseEntity<?> getFormulaire() {
        return ResponseEntity.ok(Map.of(
                "sections", FormulaireConstants.SECTIONS,
                "questions", FormulaireConstants.QUESTIONS,
                "scaleLabels", FormulaireConstants.SCALE_LABELS
        ));
    }

    @GetMapping("/api/evaluation-a-chaud/session/{sessionId}/kpis")
    public ResponseEntity<SatisfactionKpiDto> getSatisfactionKpis(
            @PathVariable Integer sessionId) {
        return ResponseEntity.ok(service.getSatisfactionKpis(sessionId));
    }
}