package com.s3m.formation.api.service.kpi;

import com.s3m.formation.api.kpi.client.dto.*;
import com.s3m.formation.api.kpi.client.projection.*;
import com.s3m.formation.api.kpi.client.repository.*;
import com.s3m.formation.domain.sessionFormation.SessionFormation;
import com.s3m.formation.domain.sessionFormation.SessionFormationRepository;
import com.s3m.formation.domain.sessionFormation.SessionFormationStatut;
import lombok.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClientKpiServiceImpl implements ClientKpiService {

    private final ClientFinancierKpiRepository financierRepo;
    private final ClientPopulationKpiRepository populationRepo;
    private final ClientParticipantsByDepartmentKpiRepository participantsDeptRepo;
    private final ClientHoursByDepartmentKpiRepository hoursDeptRepo;
    private final ClientHoursByFournisseurKpiRepository hoursFournisseurRepo;
    private final ClientHoursByFamilleFormationKpiRepository hoursFamilleRepo;
    private final ClientFormationKpiRepository formationRepo;
    private final TotalSessionsKpiRepository totalSessionsRepo;
    private final SessionFormationRepository sessionFormationRepository;


    @Override
    public ClientKpiResponse getClientKpis(Integer clientId, Integer[] years) {

        Integer[] yearsArray = resolveYears(clientId, years);

        ClientFinancierKpiProjection financierProjection = financierRepo.computeFinancier(clientId, yearsArray);
        ClientFinancierKpiDto financier = mapFinancier(financierProjection);

        List<ClientFinancierByRemboursementProjection> remboursementProj =
                financierRepo.computeFinancierByRemboursement(clientId, yearsArray);
        List<ClientFinancierByRemboursementDto> remboursementByType = remboursementProj.stream()
                .map(p -> new ClientFinancierByRemboursementDto(
                        p.getRemboursement(),
                        BigDecimal.valueOf(p.getTotalHeures())
                ))
                .toList();

        List<RepartitionItemProjection> cspProj         = populationRepo.countByCsp(clientId, yearsArray);
        List<RepartitionItemProjection> fonctionProj    = populationRepo.countByFonction(clientId);
        List<RepartitionItemProjection> typeContratProj = populationRepo.countByTypeContrat(clientId);
        List<RepartitionItemProjection> genreProj       = populationRepo.countByGenre(clientId);

        List<ClientGenderByDepartmentKpiProjection> genderDeptProj =
                populationRepo.getGenderByDepartmentForClient(clientId, yearsArray);

        List<GenderHoursKpiProjection> genderHoursProj =
                populationRepo.getTrainingHoursByGender(clientId, yearsArray);

        List<CspHoursKpiProjection> cspHoursProj =
                populationRepo.getTrainingHoursByCsp(clientId, yearsArray);

        TotalParticipantsKpiProjection participantsProj =
                populationRepo.getTotalParticipants(clientId, yearsArray);
        Long totalParticipants = participantsProj != null ? participantsProj.getTotalParticipants() : 0L;

        List<EmployeGenderByDepartmentKpiDto> genderByDepartment = genderDeptProj.stream()
                .map(p -> new EmployeGenderByDepartmentKpiDto(p.getDepartement(), p.getGenre(), p.getNombre()))
                .toList();

        List<GenderHoursKpiDto> genderHours = genderHoursProj.stream()
                .map(p -> new GenderHoursKpiDto(p.getLabel(), p.getTotalHeures(), p.getNombreEmployes()))
                .toList();

        List<CspHoursKpiDto> cspHours = cspHoursProj.stream()
                .map(p -> new CspHoursKpiDto(p.getCsp(), p.getTotalHeures(), p.getNombreEmployes()))
                .toList();

        ClientPopulationKpiDto population = new ClientPopulationKpiDto(
                mapRepartition(cspProj),
                mapRepartition(fonctionProj),
                mapRepartition(typeContratProj),
                mapRepartition(genreProj),
                genderByDepartment,
                genderHours,
                cspHours,
                totalParticipants
        );

        List<ClientParticipantsByDepartmentKpiProjection> participantsDeptProj =
                participantsDeptRepo.findByClientIdAndYears(clientId, yearsArray);
        List<ClientHoursByDepartmentKpiProjection> hoursDeptProj =
                hoursDeptRepo.findByClientIdAndYears(clientId, yearsArray);
        List<ClientHoursByFournisseurKpiProjection> hoursFournisseurProj =
                hoursFournisseurRepo.findByClientIdAndYears(clientId, yearsArray);
        List<ClientHoursByFamilleFormationKpiProjection> hoursFamilleProj =
                hoursFamilleRepo.findByClientIdAndYears(clientId, yearsArray);

        List<ClientParticipantsByDepartmentKpiDto> participantsDept = mapParticipantsByDepartment(participantsDeptProj);
        List<ClientHoursByDepartmentKpiDto>        hoursDept        = mapHoursByDepartment(hoursDeptProj);
        List<ClientHoursByFournisseurKpiDto>        hoursFournisseur = mapHoursByFournisseur(hoursFournisseurProj);
        List<ClientHoursByFamilleFormationKpiDto>   hoursFamille     = mapHoursByFamilleFormation(hoursFamilleProj);

        TotalFormationHoursProjection totalHoursProj = formationRepo.getTotalFormationHours(clientId, yearsArray);
        BigDecimal totalFormationHours = totalHoursProj != null ? totalHoursProj.getTotalHeures() : BigDecimal.ZERO;

        Long totalSessions = totalSessionsRepo
                .getTotalSessionsByClientAndYears(clientId, yearsArray)
                .getTotalSessions();

        return new ClientKpiResponse(
                financier,
                population,
                participantsDept,
                hoursDept,
                hoursFournisseur,
                hoursFamille,
                remboursementByType,
                totalFormationHours,
                totalSessions
        );
    }

    @Override
    public VisibiliteKpiDto getVisibiliteKpis(Integer clientId, LocalDate start, LocalDate end) {
        List<SessionFormation> sessions = sessionFormationRepository
                .findByStatutAndDateDebutBetweenAndEntreprise(
                        SessionFormationStatut.PLANIFIEE, start, end, clientId);

        long nbSessions = sessions.size();
        long nbZero = sessions.stream()
                .filter(s -> s.getParticipations() == null || s.getParticipations().isEmpty())
                .count();
        double moyenne = nbSessions > 0
                ? sessions.stream()
                .mapToInt(s -> s.getParticipations() != null ? s.getParticipations().size() : 0)
                .average().orElse(0)
                : 0;
        moyenne = Math.round(moyenne * 10.0) / 10.0;

        return new VisibiliteKpiDto(nbSessions, moyenne, nbZero);
    }

    @Override
    public List<VisibiliteSessionDto> getVisibiliteSessions(Integer clientId, LocalDate start, LocalDate end) {
        return sessionFormationRepository
                .findByStatutAndDateDebutBetweenAndEntreprise(
                        SessionFormationStatut.PLANIFIEE, start, end, clientId)
                .stream()
                .map(s -> new VisibiliteSessionDto(
                        s.getIdSession(),
                        s.getReferenceSession(),
                        s.getFormation() != null ? s.getFormation().getModule() : "—",
                        s.getFormateur() != null
                                ? s.getFormateur().getNom() + " " + s.getFormateur().getPrenom() : "—",
                        s.getEntreprise() != null ? s.getEntreprise().getNomEntreprise() : "—",
                        s.getDateDebut(),
                        s.getDateFin(),
                        s.getLieu(),
                        s.getParticipations() != null ? s.getParticipations().size() : 0
                ))
                .toList();
    }

    private Integer[] resolveYears(Integer clientId, Integer[] years) {
        if (years != null && years.length > 0) return years;
        List<Integer> allYears = formationRepo.findDistinctYearsByClientId(clientId);
        return allYears.toArray(new Integer[0]);
    }

    // ─── Mapping helpers ─────────────────────────────────────────────────────────

    private List<RepartitionKpiItemDto> mapRepartition(List<RepartitionItemProjection> items) {
        return items.stream()
                .map(p -> new RepartitionKpiItemDto(p.getLabel(), p.getCount()))
                .toList();
    }

    private ClientFinancierKpiDto mapFinancier(ClientFinancierKpiProjection p) {
        if (p == null) {
            return new ClientFinancierKpiDto(
                    BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }
        BigDecimal coutMoyenParJour = p.getTotalJours().compareTo(BigDecimal.ZERO) > 0
                ? p.getCoutTotal().divide(p.getTotalJours(), 2, BigDecimal.ROUND_HALF_UP)
                : BigDecimal.ZERO;
        BigDecimal coutMoyenParParticipant = p.getTotalParticipants() > 0
                ? p.getCoutTotal().divide(BigDecimal.valueOf(p.getTotalParticipants()), 2, BigDecimal.ROUND_HALF_UP)
                : BigDecimal.ZERO;
        return new ClientFinancierKpiDto(
                p.getCoutTotal(), coutMoyenParJour, coutMoyenParParticipant,
                p.getCoutRembourse(), p.getCoutNonRembourse());
    }

    private List<ClientParticipantsByDepartmentKpiDto> mapParticipantsByDepartment(
            List<ClientParticipantsByDepartmentKpiProjection> projections) {
        return projections.stream()
                .map(p -> new ClientParticipantsByDepartmentKpiDto(p.getDepartement(), p.getNbParticipants()))
                .toList();
    }

    private List<ClientHoursByDepartmentKpiDto> mapHoursByDepartment(
            List<ClientHoursByDepartmentKpiProjection> projections) {
        return projections.stream()
                .map(p -> new ClientHoursByDepartmentKpiDto(p.getDepartement(), p.getTotalHeures()))
                .toList();
    }

    private List<ClientHoursByFournisseurKpiDto> mapHoursByFournisseur(
            List<ClientHoursByFournisseurKpiProjection> projections) {
        return projections.stream()
                .map(p -> new ClientHoursByFournisseurKpiDto(p.getFournisseur(), p.getTotalHeures()))
                .toList();
    }

    private List<ClientHoursByFamilleFormationKpiDto> mapHoursByFamilleFormation(
            List<ClientHoursByFamilleFormationKpiProjection> projections) {
        return projections.stream()
                .map(p -> new ClientHoursByFamilleFormationKpiDto(p.getFamilleFormation(), p.getTotalHeures()))
                .toList();
    }

    @Override
    public List<Integer> getAvailableYears(Integer clientId) {
        return formationRepo.findDistinctYearsByClientId(clientId);
    }

    // ─── Growth KPI ──────────────────────────────────────────────────────────────

    @Override
    public TotalGrowthKpiDto getTotalGrowthKpi(
            Integer entrepriseId,
            String period,
            String month,
            Integer[] years
    ) {
        List<Object[]> rawData;
        boolean hasYears = years != null && years.length > 0;

        switch (period.toLowerCase()) {

            case "daily" -> {
                if (month == null || month.isBlank()) {
                    throw new IllegalArgumentException("Month is required for daily period (format: YYYY-MM)");
                }
                rawData = formationRepo.getTotalGrowthByDayForEntreprise(entrepriseId, month);
            }

            case "yearly" -> {
                if (hasYears) {
                    rawData = formationRepo.getTotalGrowthByYearForEntrepriseAndYears(entrepriseId, years);
                } else {
                    rawData = formationRepo.getTotalGrowthByYearForEntreprise(entrepriseId);
                }
            }

            case "monthly" -> {
                if (!hasYears) {
                    // No filter — show all months across all time
                    rawData = formationRepo.getTotalGrowthByMonthForEntreprise(entrepriseId);
                } else if (years.length == 1) {
                    // Single year — show "Jan YYYY", "Feb YYYY", ...
                    rawData = formationRepo.getTotalGrowthByMonthForEntrepriseAndYear(entrepriseId, years[0]);
                } else {
                    // Multiple years — show "Jan", "Feb", ... summed across selected years
                    rawData = formationRepo.getTotalGrowthByMonthForEntrepriseAndYears(entrepriseId, years);
                }
            }

            default -> throw new IllegalArgumentException(
                    "Invalid period: " + period + ". Allowed: daily, monthly, yearly");
        }

        if (rawData == null || rawData.isEmpty()) {
            TotalGrowthKpiDto empty = new TotalGrowthKpiDto();
            empty.setCategories(List.of());
            empty.setSeries(List.of());
            empty.setTopFormationsByMonth(Map.of());
            return empty;
        }

        List<String> categories = new ArrayList<>();
        List<Double>  topData   = new ArrayList<>();
        List<Double>  autresData = new ArrayList<>();
        Map<String, String> topFormationsByPeriod = new LinkedHashMap<>();

        for (Object[] row : rawData) {
            String periodLabel  = String.valueOf(row[0]);
            String topFormation = String.valueOf(row[1]);
            Double topHours     = row[2] != null ? ((Number) row[2]).doubleValue() : 0.0;
            Double autresHours  = row[3] != null ? ((Number) row[3]).doubleValue() : 0.0;

            categories.add(periodLabel);
            topData.add(topHours);
            autresData.add(autresHours);
            topFormationsByPeriod.put(periodLabel, topFormation);
        }

        TotalGrowthKpiDto dto = new TotalGrowthKpiDto();
        dto.setCategories(categories);
        dto.setSeries(List.of(
                new TotalGrowthKpiDto.SeriesData("Top Formation", topData),
                new TotalGrowthKpiDto.SeriesData("Autres", autresData)
        ));
        dto.setTopFormationsByMonth(topFormationsByPeriod);
        return dto;
    }
}
