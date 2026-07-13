package com.s3m.formation.domain.quiz;

import com.s3m.formation.api.dto.*;
import com.s3m.formation.domain.employe.EmployeRepository;
import com.s3m.formation.domain.sessionFormation.SessionFormation;
import com.s3m.formation.domain.sessionFormation.SessionFormationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class QuizService {

    private final QuizReponseRepository repository;
    private final SessionFormationRepository sessionRepository;
    private final EmployeRepository employeRepository;

    public void submit(QuizSubmitRequest req) {
        if (repository.existsBySession_IdSessionAndEmploye_IdEmploye(
                req.idSession(), req.idEmploye())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Vous avez déjà soumis ce quiz pour cette session.");
        }

        var session = sessionRepository.findById(req.idSession())
                .orElseThrow(() -> new EntityNotFoundException("Session not found"));
        var employe = employeRepository.findById(req.idEmploye())
                .orElseThrow(() -> new EntityNotFoundException("Employé not found"));

        // Auto-grade
        int score = 0;
        for (QuizConstants.Question q : QuizConstants.QUESTIONS) {
            String given = req.reponses().get(String.valueOf(q.id()));
            if (given != null && given.equalsIgnoreCase(q.correctAnswer())) {
                score++;
            }
        }

        QuizReponse reponse = QuizReponse.builder()
                .session(session)
                .employe(employe)
                .reponses(req.reponses())
                .score(score)
                .debutLe(req.debutLe())
                .soumisLe(LocalDateTime.now())
                .build();

        repository.save(reponse);
    }

    @Transactional(readOnly = true)
    public QuizStatsDto getStats(Integer sessionId) {
        var session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found"));

        int totalParticipants = session.getParticipations() != null
                ? session.getParticipations().size() : 0;

        List<QuizReponse> all = repository.findBySession_IdSession(sessionId);
        String formateur = session.getFormateur() != null
                ? session.getFormateur().getNom() + " " + session.getFormateur().getPrenom()
                : "—";
        if (all.isEmpty()) {
            return new QuizStatsDto(sessionId,
                    session.getFormation().getModule(),formateur,
                    0, totalParticipants, 0, Map.of(), List.of());
        }

        double scoreMoyen = all.stream()
                .filter(r -> r.getScore() != null)
                .mapToInt(QuizReponse::getScore)
                .average().orElse(0);
        scoreMoyen = Math.round(scoreMoyen * 10.0) / 10.0;

        // Distribution: for each question, count how many picked each answer
        Map<String, Map<String, Integer>> distribution = new LinkedHashMap<>();
        for (QuizConstants.Question q : QuizConstants.QUESTIONS) {
            String qId = String.valueOf(q.id());
            Map<String, Integer> counts = new LinkedHashMap<>();
            for (QuizReponse r : all) {
                String answer = r.getReponses().get(qId);
                if (answer != null) {
                    counts.merge(answer, 1, Integer::sum);
                }
            }
            distribution.put(qId, counts);
        }

        List<QuizReponseDto> dtos = all.stream()
                .map(r -> new QuizReponseDto(
                        r.getIdReponse(),
                        r.getEmploye().getNom() + " " + r.getEmploye().getPrenom(),
                        r.getReponses(),
                        r.getScore(),
                        r.getSoumisLe(),
                        r.getDebutLe()
                )).toList();

        return new QuizStatsDto(sessionId,
                session.getFormation().getModule(),
                formateur,
                all.size(), totalParticipants,
                scoreMoyen, distribution, dtos);
    }

    @Transactional(readOnly = true)
    public List<QuizSummaryDto> getSummaryForEntreprise(Integer entrepriseId) {
        List<SessionFormation> sessions =
                sessionRepository.findByEntreprise_IdEntreprise(entrepriseId);

        if (sessions.isEmpty()) return List.of();

        List<Integer> sessionIds = sessions.stream()
                .map(SessionFormation::getIdSession)
                .toList();

        // One query for ALL responses across all sessions
        List<QuizReponse> allReponses = repository.findBySessionIdIn(sessionIds);

        // Group by session id in memory
        Map<Integer, List<QuizReponse>> bySession = allReponses.stream()
                .collect(Collectors.groupingBy(r -> r.getSession().getIdSession()));

        return sessions.stream()
                .map(session -> {
                    List<QuizReponse> reponses =
                            bySession.getOrDefault(session.getIdSession(), List.of());
                    if (reponses.isEmpty()) return null;

                    double scoreMoyen = reponses.stream()
                            .filter(r -> r.getScore() != null)
                            .mapToInt(QuizReponse::getScore)
                            .average().orElse(0);
                    scoreMoyen = Math.round(scoreMoyen * 10.0) / 10.0;

                    LocalDateTime derniere = reponses.stream()
                            .map(QuizReponse::getSoumisLe)
                            .filter(Objects::nonNull)
                            .max(LocalDateTime::compareTo)
                            .orElse(null);
                    String formateur = session.getFormateur() != null
                            ? session.getFormateur().getNom() + " " + session.getFormateur().getPrenom()
                            : "—";

                    return new QuizSummaryDto(
                            session.getIdSession(),
                            session.getReferenceSession(),
                            session.getFormation() != null
                                    ? session.getFormation().getModule() : "",
                            formateur,
                            reponses.size(),
                            session.getParticipations() != null
                                    ? session.getParticipations().size() : 0,
                            scoreMoyen,
                            derniere
                    );
                })
                .filter(Objects::nonNull)
                .toList();
    }
}