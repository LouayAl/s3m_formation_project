package com.s3m.formation.api.service.kpi;

import com.s3m.formation.api.kpi.client.dto.*;
import com.s3m.formation.domain.EvaluationAFroid.EvaluationAFroid;
import com.s3m.formation.domain.EvaluationAFroid.EvaluationAFroidRepository;
import com.s3m.formation.domain.coutFormation.CoutFormation;
import com.s3m.formation.domain.coutFormation.CoutFormationRepository;
import com.s3m.formation.domain.employe.Employe;
import com.s3m.formation.domain.employe.EmployeRepository;
import com.s3m.formation.domain.evaluationAChaud.EvaluationAChaudRepository;
import com.s3m.formation.domain.formation.FormationRepository;
import com.s3m.formation.domain.participation.ParticipationRepository;
import com.s3m.formation.domain.sessionFormation.SessionFormation;
import com.s3m.formation.domain.sessionFormation.SessionFormationRepository;
import lombok.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientKpiServiceImpl implements ClientKpiService {

    private final SessionFormationRepository sessionRepo;
    private final ParticipationRepository participationRepo;
    private final CoutFormationRepository coutRepo;
    private final EmployeRepository employeRepo;
    private final EvaluationAChaudRepository evaluationAChaudRepo;
    private final EvaluationAFroidRepository evaluationAFroidRepo;
    private final FormationRepository formationRepo;

    @Override
    public ClientKpiResponse getClientKpis(Integer clientId) {

        // 1️⃣ Identité
        ClientIdentiteKpiDto identite = calculateIdentite(clientId);

        // 2️⃣ Volume
        ClientVolumeKpiDto volume = calculateVolume(clientId);

        // 3️⃣ Financier
        ClientFinancierKpiDto financier = calculateFinancier(clientId);

        // 4️⃣ Formations
        ClientFormationKpiDto formations = calculateFormations(clientId);

        // 5️⃣ Population
        ClientPopulationKpiDto population = calculatePopulation(clientId);

        // 6️⃣ Efficacité
        ClientEfficaciteKpiDto efficacite = calculateEfficacite(clientId);

        return new ClientKpiResponse(
                identite,
                volume,
                financier,
                formations,
                population,
                efficacite
        );
    }

    private ClientEfficaciteKpiDto calculateEfficacite(Integer clientId) {
        List<EvaluationAFroid> evals = evaluationAFroidRepo.findByParticipation_Session_Entreprise_IdEntreprise(clientId);

        if (evals.isEmpty()) return null;

        long total = evals.size();
        long evalCount = evals.stream().filter(e -> e.getTauxEfficacite() != null).count();
        BigDecimal tauxMoyen = evals.stream()
                .filter(e -> e.getTauxEfficacite() != null)
                .map(EvaluationAFroid::getTauxEfficacite)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(evalCount), 2, BigDecimal.ROUND_HALF_UP);

        LocalDate lastEval = evals.stream()
                .map(EvaluationAFroid::getDateEvaluationAFroid)
                .filter(Objects::nonNull)
                .max(LocalDate::compareTo)
                .orElse(null);

        BigDecimal pourcentageEvalue = BigDecimal.valueOf(evalCount * 100.0 / total);

        return new ClientEfficaciteKpiDto(pourcentageEvalue, tauxMoyen, lastEval);
    }


    private ClientPopulationKpiDto calculatePopulation(Integer clientId) {
        List<Employe> employes = employeRepo.findByEntreprise_IdEntreprise(clientId);

        Map<String, Long> cspMap = employes.stream().collect(Collectors.groupingBy(
                e -> e.getCsp() != null ? e.getCsp() : "Non renseigné", Collectors.counting()
        ));
        Map<String, Long> fonctionMap = employes.stream().collect(Collectors.groupingBy(
                e -> e.getFonction() != null ? e.getFonction() : "Non renseigné", Collectors.counting()
        ));
        Map<String, Long> typeContratMap = employes.stream().collect(Collectors.groupingBy(
                e -> e.getTypeContrat() != null ? e.getTypeContrat() : "Non renseigné", Collectors.counting()
        ));
        Map<String, Long> genreMap = employes.stream().collect(Collectors.groupingBy(
                e -> e.getF_h() != null ? e.getF_h() : "Non renseigné", Collectors.counting()
        ));

        List<RepartitionKpiItemDto> csp = cspMap.entrySet().stream()
                .map(e -> new RepartitionKpiItemDto(e.getKey(), e.getValue()))
                .toList();
        List<RepartitionKpiItemDto> fonction = fonctionMap.entrySet().stream()
                .map(e -> new RepartitionKpiItemDto(e.getKey(), e.getValue()))
                .toList();
        List<RepartitionKpiItemDto> typeContrat = typeContratMap.entrySet().stream()
                .map(e -> new RepartitionKpiItemDto(e.getKey(), e.getValue()))
                .toList();
        List<RepartitionKpiItemDto> genre = genreMap.entrySet().stream()
                .map(e -> new RepartitionKpiItemDto(e.getKey(), e.getValue()))
                .toList();

        return new ClientPopulationKpiDto(csp, fonction, typeContrat, genre);
    }


    private ClientFormationKpiDto calculateFormations(Integer clientId)     {
        List<SessionFormation> sessions = sessionRepo.findByEntreprise_IdEntreprise(clientId);

        Map<String, Long> formationCount = new HashMap<>();
        Map<String, Long> familleCount = new HashMap<>();
        long total = 0;
        long interne = 0;
        long externe = 0;

        for (SessionFormation s : sessions) {
            String formation = s.getFormation().getModule();
            String famille = s.getFormation().getFamilleFormation();
            formationCount.put(formation, formationCount.getOrDefault(formation, 0L) + 1);
            familleCount.put(famille, familleCount.getOrDefault(famille, 0L) + 1);

            total++;
            if ("INTERNE".equalsIgnoreCase(s.getFormation().getInterneExterne())) interne++;
            else if ("EXTERNE".equalsIgnoreCase(s.getFormation().getInterneExterne())) externe++;
        }

        String formationLaPlusSuivie = formationCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).orElse(null);

        String famillePrincipale = familleCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).orElse(null);

        BigDecimal pourcentageInterne = total > 0 ? BigDecimal.valueOf(interne * 100.0 / total) : BigDecimal.ZERO;
        BigDecimal pourcentageExterne = total > 0 ? BigDecimal.valueOf(externe * 100.0 / total) : BigDecimal.ZERO;

        return new ClientFormationKpiDto(
                formationCount.size(),
                formationLaPlusSuivie,
                famillePrincipale,
                pourcentageInterne,
                pourcentageExterne
        );
    }


    private ClientFinancierKpiDto calculateFinancier(Integer clientId) {
        List<SessionFormation> sessions = sessionRepo.findByEntreprise_IdEntreprise(clientId);

        BigDecimal coutTotal = BigDecimal.ZERO;
        BigDecimal coutRembourse = BigDecimal.ZERO;
        BigDecimal coutNonRembourse = BigDecimal.ZERO;
        BigDecimal totalJours = BigDecimal.ZERO;
        long totalParticipants = 0;

        for (SessionFormation s : sessions) {
            List<CoutFormation> couts = coutRepo.findBySession_IdSession(s.getIdSession());

            for (CoutFormation c : couts) {
                coutTotal = coutTotal.add(c.getCoutTotal());
                totalJours = totalJours.add(s.getDJours() != null ? s.getDJours() : BigDecimal.ZERO);

                if ("OUI".equalsIgnoreCase(c.getRemboursement())) coutRembourse = coutRembourse.add(c.getCoutTotal());
                else coutNonRembourse = coutNonRembourse.add(c.getCoutTotal());
            }

            totalParticipants += participationRepo.countBySession_IdSession(s.getIdSession());
        }

        BigDecimal coutMoyenParJour = totalJours.compareTo(BigDecimal.ZERO) > 0
                ? coutTotal.divide(totalJours, 2, BigDecimal.ROUND_HALF_UP)
                : BigDecimal.ZERO;

        BigDecimal coutMoyenParParticipant = totalParticipants > 0
                ? coutTotal.divide(BigDecimal.valueOf(totalParticipants), 2, BigDecimal.ROUND_HALF_UP)
                : BigDecimal.ZERO;

        return new ClientFinancierKpiDto(
                coutTotal,
                coutMoyenParJour,
                coutMoyenParParticipant,
                coutRembourse,
                coutNonRembourse
        );
    }


    private ClientVolumeKpiDto calculateVolume(Integer clientId) {
        List<SessionFormation> sessions = sessionRepo.findByEntreprise_IdEntreprise(clientId);

        int totalSessions = sessions.size();

        long totalParticipants = 0;
        BigDecimal totalJours = BigDecimal.ZERO;
        BigDecimal totalHeures = BigDecimal.ZERO;

        for (SessionFormation s : sessions) {
            totalParticipants += participationRepo.countBySession_IdSession(s.getIdSession());
            if (s.getDJours() != null) totalJours = totalJours.add(s.getDJours());
            if (s.getDHeures() != null) totalHeures = totalHeures.add(s.getDHeures());
        }

        return new ClientVolumeKpiDto(
                totalSessions,
                (int) totalParticipants,
                totalJours,
                totalHeures
        );
    }


    // TODO: Implement the private helper methods for each KPI block
    private ClientIdentiteKpiDto calculateIdentite(Integer clientId) {
        Integer premiereAnnee = sessionRepo.findPremiereAnnee(clientId);
        Integer derniereAnnee = sessionRepo.findDerniereAnnee(clientId);
        LocalDate datePremiere = sessionRepo.findDatePremiereFormation(clientId);
        LocalDate dateDerniere = sessionRepo.findDateDerniereFormation(clientId);
        String nomClient = sessionRepo.findNomClient(clientId);

        return new ClientIdentiteKpiDto(
                clientId,
                nomClient,
                premiereAnnee,
                derniereAnnee,
                datePremiere,
                dateDerniere
        );
    }
}