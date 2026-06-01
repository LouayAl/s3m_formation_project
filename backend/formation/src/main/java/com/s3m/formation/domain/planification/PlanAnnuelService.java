// src/main/java/com/s3m/formation/domain/planification/PlanAnnuelService.java
package com.s3m.formation.domain.planification;

import com.s3m.formation.domain.entreprise.Entreprise;
import com.s3m.formation.domain.entreprise.EntrepriseRepository;
import com.s3m.formation.domain.sessionFormation.SessionFormation;
import com.s3m.formation.domain.sessionFormation.SessionFormationRepository;
import com.s3m.formation.domain.sessionFormation.SessionFormationStatut;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlanAnnuelService {

    private final PlanAnnuelRepository     planRepo;
    private final EntrepriseRepository     entrepriseRepo;
    private final SessionFormationRepository sessionRepo;

    private static final String[] MONTH_LABELS =
            { "Jan", "Fév", "Mar", "Avr", "Mai", "Juin", "Juil", "Août", "Sep", "Oct", "Nov", "Déc" };

    // ── GET plan + actuals for a year + entreprise ────────────────────────────
    public PlanAnnuelDto getPlan(Integer annee, Integer entrepriseId) {
        Entreprise entreprise = entrepriseRepo.findById(entrepriseId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Entreprise non trouvée"));

        // Targets — zero if no plan saved yet
        int[] targets = new int[12];
        planRepo.findByAnneeAndEntreprise_IdEntreprise(annee, entrepriseId)
                .ifPresent(p -> {
                    targets[0]  = p.getJan(); targets[1]  = p.getFev();
                    targets[2]  = p.getMar(); targets[3]  = p.getAvr();
                    targets[4]  = p.getMai(); targets[5]  = p.getJui();
                    targets[6]  = p.getJul(); targets[7]  = p.getAou();
                    targets[8]  = p.getSep(); targets[9]  = p.getOct();
                    targets[10] = p.getNov(); targets[11] = p.getDec();
                });

        // Actuals — dedicated query avoids the PostgreSQL null-param type issue
        LocalDate start = LocalDate.of(annee, 1, 1);
        LocalDate end   = LocalDate.of(annee, 12, 31);

        int[] actuals = new int[12];
        sessionRepo
                .findTermineesForEntrepriseAndYear(entrepriseId, start, end)
                .forEach(s -> {
                    if (s.getDateDebut() != null) {
                        actuals[s.getDateDebut().getMonthValue() - 1]++;
                    }
                });

        // Build response
        List<PlanAnnuelDto.MonthData> months = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            months.add(new PlanAnnuelDto.MonthData(
                    i + 1, MONTH_LABELS[i], targets[i], actuals[i]
            ));
        }

        return new PlanAnnuelDto(annee, entrepriseId, entreprise.getNomEntreprise(), months);
    }
    // ── SAVE (upsert) ─────────────────────────────────────────────────────────
    @Transactional
    public PlanAnnuelDto savePlan(PlanAnnuelRequest req) {
        Entreprise entreprise = entrepriseRepo.findById(req.entrepriseId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Entreprise non trouvée"));

        PlanAnnuel plan = planRepo
                .findByAnneeAndEntreprise_IdEntreprise(req.annee(), req.entrepriseId())
                .orElseGet(() -> PlanAnnuel.builder()
                        .annee(req.annee())
                        .entreprise(entreprise)
                        .build());

        plan.setJan(req.jan()); plan.setFev(req.fev());
        plan.setMar(req.mar()); plan.setAvr(req.avr());
        plan.setMai(req.mai()); plan.setJui(req.jui());
        plan.setJul(req.jul()); plan.setAou(req.aou());
        plan.setSep(req.sep()); plan.setOct(req.oct());
        plan.setNov(req.nov()); plan.setDec(req.dec());

        planRepo.save(plan);
        return getPlan(req.annee(), req.entrepriseId());
    }
}