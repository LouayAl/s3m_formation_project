package com.s3m.formation.domain.evaluationAChaud;

import com.s3m.formation.api.dto.*;
import com.s3m.formation.domain.employe.Employe;
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

@Service
@RequiredArgsConstructor
@Transactional
public class EvaluationAChaudService {

    private final EvaluationAChaudRepository repository;
    private final EvaluationReponseRepository reponseRepository;
    private final SessionFormationRepository sessionRepository;
    private final EmployeRepository employeRepository;

    public void submit(EvaluationAChaudRequest req) {
        if (repository.existsBySession_IdSessionAndEmploye_IdEmploye(
                req.idSession(), req.idEmploye())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Vous avez déjà soumis une évaluation pour cette session.");
        }

        SessionFormation session = sessionRepository.findById(req.idSession())
                .orElseThrow(() -> new EntityNotFoundException("Session not found"));
        Employe employe = employeRepository.findById(req.idEmploye())
                .orElseThrow(() -> new EntityNotFoundException("Employé not found"));

        EvaluationAChaud eval = EvaluationAChaud.builder()
                .session(session)
                .employe(employe)
                .jourEvaluation(null)   // no longer used
                .commentaire(req.commentaire())
                .soumisLe(LocalDateTime.now())
                .build();

        EvaluationAChaud saved = repository.save(eval);

        req.reponses().forEach((questionId, score) -> {
            EvaluationReponse reponse = EvaluationReponse.builder()
                    .evalChaud(saved)
                    .idQuestion(questionId)
                    .score(score)
                    .build();
            reponseRepository.save(reponse);
        });
    }

    @Transactional(readOnly = true)
    public EvaluationAChaudStatsDto getStats(Integer sessionId) {
        SessionFormation session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found"));

        int totalParticipants = session.getParticipations() != null
                ? session.getParticipations().size() : 0;

        List<EvaluationAChaud> allEvals = repository.findBySession_IdSession(sessionId);

        if (allEvals.isEmpty()) {
            return new EvaluationAChaudStatsDto(
                    sessionId, session.getFormation().getModule(),
                    0, totalParticipants, 0, Map.of(), List.of()
            );
        }

        // Aggregate all scores across all evaluations
        Map<Integer, List<Integer>> scoresByQuestion = new HashMap<>();
        for (EvaluationAChaud eval : allEvals) {
            if (eval.getReponses() != null) {
                for (EvaluationReponse r : eval.getReponses()) {
                    scoresByQuestion
                            .computeIfAbsent(r.getIdQuestion(), k -> new ArrayList<>())
                            .add(r.getScore());
                }
            }
        }

        Map<Integer, Double> moyennesParQuestion = new LinkedHashMap<>();
        for (int qId = 1; qId <= 13; qId++) {
            List<Integer> scores = scoresByQuestion.getOrDefault(qId, List.of());
            double avg = scores.isEmpty() ? 0
                    : round(scores.stream().mapToInt(i -> i).average().orElse(0));
            moyennesParQuestion.put(qId, avg);
        }

        double moyenneGlobale = round(
                moyennesParQuestion.values().stream()
                        .mapToDouble(Double::doubleValue)
                        .average().orElse(0)
        );

        List<EvaluationAChaudResponseDto> reponses = allEvals.stream()
                .map(this::toDto)
                .toList();

        return new EvaluationAChaudStatsDto(
                sessionId, session.getFormation().getModule(),
                allEvals.size(), totalParticipants, moyenneGlobale,
                moyennesParQuestion, reponses
        );
    }

    private double round(double val) {
        return Math.round(val * 10.0) / 10.0;
    }

    private EvaluationAChaudResponseDto toDto(EvaluationAChaud e) {
        List<EvaluationReponseDto> reponseDtos = e.getReponses() != null
                ? e.getReponses().stream()
                .map(r -> new EvaluationReponseDto(r.getIdQuestion(), r.getScore()))
                .sorted(Comparator.comparing(EvaluationReponseDto::idQuestion))
                .toList()
                : List.of();

        return new EvaluationAChaudResponseDto(
                e.getIdEvalChaud(),
                e.getSession().getIdSession(),
                e.getEmploye().getNom() + " " + e.getEmploye().getPrenom(),
                e.getJourEvaluation(), // will be null, kept for DTO compatibility
                reponseDtos,
                e.getCommentaire(),
                e.getSoumisLe()
        );
    }

    @Transactional(readOnly = true)
    public List<EvaluationSummaryDto> getSummaryForEntreprise(Integer entrepriseId) {
        return sessionRepository.findByEntreprise_IdEntreprise(entrepriseId)
                .stream()
                .map(session -> {
                    List<EvaluationAChaud> evals =
                            repository.findBySession_IdSession(session.getIdSession());

                    double moyenneGlobale = 0;
                    LocalDateTime derniere = null;

                    if (!evals.isEmpty()) {
                        List<Integer> allScores = evals.stream()
                                .filter(e -> e.getReponses() != null)
                                .flatMap(e -> e.getReponses().stream())
                                .map(EvaluationReponse::getScore)
                                .toList();
                        if (!allScores.isEmpty()) {
                            moyenneGlobale = Math.round(
                                    allScores.stream().mapToInt(i -> i).average().orElse(0) * 10.0
                            ) / 10.0;
                        }
                        derniere = evals.stream()
                                .map(EvaluationAChaud::getSoumisLe)
                                .filter(Objects::nonNull)
                                .max(LocalDateTime::compareTo)
                                .orElse(null);
                    }

                    return new EvaluationSummaryDto(
                            session.getIdSession(),
                            session.getReferenceSession(),
                            session.getFormation() != null
                                    ? session.getFormation().getModule() : "",
                            evals.size(),
                            session.getParticipations() != null
                                    ? session.getParticipations().size() : 0,
                            moyenneGlobale,
                            derniere
                    );
                })
                .filter(s -> s.totalReponses() > 0)
                .toList();
    }

    @Transactional(readOnly = true)
    public SatisfactionKpiDto getSatisfactionKpis(Integer sessionId) {
        SessionFormation session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found"));

        Integer entrepriseId = session.getEntreprise().getIdEntreprise();
        Integer formationId  = session.getFormation().getIdFormation();

        double s3m       = round(repository.findAvgScoreGlobalS3M().orElse(0.0));
        double client    = round(repository.findAvgScoreByEntreprise(entrepriseId).orElse(0.0));
        double formation = round(repository.findAvgScoreByEntrepriseAndFormation(
                entrepriseId, formationId).orElse(0.0));
        double sessionAvg = round(repository.findAvgScoreBySession(sessionId).orElse(0.0));

        return new SatisfactionKpiDto(
                s3m, client, formation, sessionAvg,
                repository.countReponsesGlobalS3M(),
                repository.countReponsesForEntreprise(entrepriseId),
                repository.countReponsesForEntrepriseAndFormation(entrepriseId, formationId),
                repository.countReponsesForSession(sessionId)
        );
    }
}