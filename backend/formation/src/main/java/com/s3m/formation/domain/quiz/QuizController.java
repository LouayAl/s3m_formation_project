package com.s3m.formation.domain.quiz;

import com.s3m.formation.api.dto.*;
import com.s3m.formation.domain.participation.ParticipationRepository;
import com.s3m.formation.domain.sessionFormation.SessionFormation;
import com.s3m.formation.domain.sessionFormation.SessionFormationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class QuizController {

    private final QuizService service;
    private final SessionFormationRepository sessionRepository;

    private static final Integer QUIZ_ENTREPRISE_ID = 42;

    private void assertQuizEntreprise(Integer sessionId) {
        SessionFormation session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found"));
        if (session.getEntreprise() == null ||
                !QUIZ_ENTREPRISE_ID.equals(session.getEntreprise().getIdEntreprise())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Quiz non disponible pour cette session.");
        }
    }

    // ── PUBLIC ────────────────────────────────────────────────────────────────

    @PostMapping("/api/public/quiz/submit")
    public ResponseEntity<Void> submit(@RequestBody QuizSubmitRequest req) {
        service.submit(req);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/public/quiz/questions")
    public ResponseEntity<?> getQuestions() {
        return ResponseEntity.ok(Map.of("questions", QuizConstants.QUESTIONS));
    }

    // ── PROTECTED ─────────────────────────────────────────────────────────────

    @GetMapping("/api/quiz/session/{sessionId}/stats")
    public ResponseEntity<QuizStatsDto> getStats(@PathVariable Integer sessionId) {
        assertQuizEntreprise(sessionId);
        return ResponseEntity.ok(service.getStats(sessionId));
    }

    @GetMapping("/api/quiz/summary")
    public ResponseEntity<List<QuizSummaryDto>> getSummary(
            org.springframework.security.core.Authentication auth) {
        Integer entrepriseId = (Integer) auth.getDetails();
        if (!QUIZ_ENTREPRISE_ID.equals(entrepriseId)) {
            return ResponseEntity.ok(List.of()); // empty list for other clients
        }
        return ResponseEntity.ok(service.getSummaryForEntreprise(entrepriseId));
    }
}