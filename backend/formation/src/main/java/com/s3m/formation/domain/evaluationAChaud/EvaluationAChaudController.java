package com.s3m.formation.domain.evaluationAChaud;

import com.s3m.formation.api.dto.EvaluationAChaudRequest;
import com.s3m.formation.api.dto.EvaluationAChaudStatsDto;
import com.s3m.formation.api.dto.EvaluationSummaryDto;
import com.s3m.formation.api.dto.SatisfactionKpiDto;
import com.s3m.formation.domain.employe.Employe;
import com.s3m.formation.domain.participation.ParticipationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

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

    // ── EXPORTS ──────────────────────────────────────────────────────────────

    @GetMapping("/api/evaluation-a-chaud/session/{sessionId}/export/pdf")
    public ResponseEntity<byte[]> exportPdf(@PathVariable Integer sessionId) {
        byte[] pdf = service.exportPdf(sessionId);
        EvaluationAChaudStatsDto stats = service.getStats(sessionId);
        String filename = buildFilename(stats.referenceSession(), stats.moduleFormation(), "pdf");

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filename + "\"")  // ← Simple, ASCII-only
                .body(pdf);
    }

    @GetMapping("/api/evaluation-a-chaud/session/{sessionId}/export/excel")
    public ResponseEntity<byte[]> exportExcel(@PathVariable Integer sessionId) {
        byte[] excel = service.exportExcel(sessionId);
        EvaluationAChaudStatsDto stats = service.getStats(sessionId);
        String filename = buildFilename(stats.referenceSession(), stats.moduleFormation(), "xlsx");

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filename + "\"")  // ← Simple, ASCII-only
                .body(excel);
    }

    private static final Pattern NON_ALNUM = Pattern.compile("[^a-zA-Z0-9_-]");

    private String buildFilename(String referenceSession, String moduleFormation, String ext) {
        String base = "evaluation_" + referenceSession + "_" + moduleFormation;
        String normalized = Normalizer.normalize(base, Normalizer.Form.NFD)
                .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        String safe = NON_ALNUM.matcher(normalized.replace(' ', '_')).replaceAll("");
        return safe + "." + ext;
    }
}