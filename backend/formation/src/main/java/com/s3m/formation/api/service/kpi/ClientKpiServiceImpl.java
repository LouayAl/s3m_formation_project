package com.s3m.formation.api.service.kpi;

import com.s3m.formation.api.kpi.client.dto.*;
import com.s3m.formation.api.kpi.client.projection.*;
import com.s3m.formation.api.kpi.client.repository.*;
import com.s3m.formation.domain.participation.Participation;
import com.s3m.formation.domain.sessionFormation.SessionFormation;
import com.s3m.formation.domain.sessionFormation.SessionFormationRepository;
import com.s3m.formation.domain.sessionFormation.SessionFormationStatut;
import lombok.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

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

    // ─── Department helpers ────────────────────────────────────────────────────

    /** Number of a session's participants belonging to departementId (or total, if departementId is null). */
    private long countDeptParticipants(SessionFormation s, Integer departementId) {
        List<Participation> parts = s.getParticipations();
        if (parts == null) return 0;
        if (departementId == null) return parts.size();
        return parts.stream()
                .filter(p -> p.getEmploye() != null
                        && p.getEmploye().getDepartement() != null
                        && departementId.equals(p.getEmploye().getDepartement().getId()))
                .count();
    }

    /** A session "belongs" to a department the moment it has at least one of that department's participants. */
    private boolean sessionTouchesDepartement(SessionFormation s, Integer departementId) {
        return departementId == null || countDeptParticipants(s, departementId) > 0;
    }

    private SessionStatusKpiDto buildStatusKpi(
            String key,
            Map<String, BigDecimal> hoursMap,
            Map<String, Long> sessionsMap,
            Map<String, Long> participantsMap
    ) {
        return new SessionStatusKpiDto(
                hoursMap.getOrDefault(key, BigDecimal.ZERO),
                sessionsMap.getOrDefault(key, 0L),
                participantsMap.getOrDefault(key, 0L)
        );
    }

    @Override
    public ClientKpiResponse getClientKpis(Integer clientId, Integer departementId, Integer[] years) {

        Integer[] yearsArray = resolveYears(clientId, departementId, years);

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

        List<RepartitionItemProjection> cspProj         = populationRepo.countByCsp(clientId, departementId, yearsArray);
        List<RepartitionItemProjection> fonctionProj    = populationRepo.countByFonction(clientId, departementId);
        List<RepartitionItemProjection> typeContratProj = populationRepo.countByTypeContrat(clientId, departementId);
        List<RepartitionItemProjection> genreProj       = populationRepo.countByGenre(clientId, departementId);

        List<ClientGenderByDepartmentKpiProjection> genderDeptProj =
                populationRepo.getGenderByDepartmentForClient(clientId, departementId, yearsArray);

        List<GenderHoursKpiProjection> genderHoursProj =
                populationRepo.getTrainingHoursByGender(clientId, departementId, yearsArray);

        List<CspHoursKpiProjection> cspHoursProj =
                populationRepo.getTrainingHoursByCsp(clientId, departementId, yearsArray);

        TotalParticipantsKpiProjection participantsProj =
                populationRepo.getTotalParticipants(clientId, departementId, yearsArray);
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

        // ⚠️ Exception charts: "Participants par Département" and "Heures par Département"
        // ALWAYS show every department, unfiltered — for admin (regardless of any filter)
        // and for department-scoped managers alike. departementId is intentionally NOT
        // passed to these two calls.
        List<ClientParticipantsByDepartmentKpiProjection> participantsDeptProj =
                participantsDeptRepo.findByClientIdAndYears(clientId, yearsArray);
        List<ClientHoursByDepartmentKpiProjection> hoursDeptProj =
                hoursDeptRepo.findByClientIdAndYears(clientId, yearsArray);

        List<ClientHoursByFournisseurKpiProjection> hoursFournisseurProj =
                hoursFournisseurRepo.findByClientIdAndYears(clientId, departementId, yearsArray);
        List<ClientHoursByFamilleFormationKpiProjection> hoursFamilleProj =
                hoursFamilleRepo.findByClientIdAndYears(clientId, departementId, yearsArray);

        List<ClientParticipantsByDepartmentKpiDto> participantsDept = mapParticipantsByDepartment(participantsDeptProj);
        List<ClientHoursByDepartmentKpiDto>        hoursDept        = mapHoursByDepartment(hoursDeptProj);
        List<ClientHoursByFournisseurKpiDto>        hoursFournisseur = mapHoursByFournisseur(hoursFournisseurProj);
        List<ClientHoursByFamilleFormationKpiDto>   hoursFamille     = mapHoursByFamilleFormation(hoursFamilleProj);

        TotalFormationHoursProjection totalHoursProj = formationRepo.getTotalFormationHours(clientId, departementId, yearsArray);
        BigDecimal totalFormationHours = totalHoursProj != null ? totalHoursProj.getTotalHeures() : BigDecimal.ZERO;

        Long totalSessions = totalSessionsRepo
                .getTotalSessionsByClientAndYears(clientId, departementId, yearsArray)
                .getTotalSessions();

        List<Object[]> hoursByStatus        = formationRepo.getFormationHoursByStatusGroup(clientId, departementId, yearsArray);
        List<Object[]> sessionsByStatus     = totalSessionsRepo.getSessionsByStatusGroup(clientId, departementId, yearsArray);
        List<Object[]> participantsByStatus = populationRepo.getParticipantsByStatusGroup(clientId, departementId, yearsArray);

        Map<String, BigDecimal> hoursMap = new HashMap<>();
        for (Object[] row : hoursByStatus) {
            hoursMap.put((String) row[0], row[1] != null ? new BigDecimal(row[1].toString()) : BigDecimal.ZERO);
        }
        Map<String, Long> sessionsMap = new HashMap<>();
        for (Object[] row : sessionsByStatus) {
            sessionsMap.put((String) row[0], ((Number) row[1]).longValue());
        }
        Map<String, Long> participantsMap = new HashMap<>();
        for (Object[] row : participantsByStatus) {
            participantsMap.put((String) row[0], ((Number) row[1]).longValue());
        }

        SessionStatusKpiDto realiseeKpi  = buildStatusKpi("REALISEE", hoursMap, sessionsMap, participantsMap);
        SessionStatusKpiDto planifieeKpi = buildStatusKpi("PLANIFIEE", hoursMap, sessionsMap, participantsMap);
        SessionStatusKpiDto autresKpi    = buildStatusKpi("AUTRE", hoursMap, sessionsMap, participantsMap);

        return new ClientKpiResponse(
                financier,
                population,
                participantsDept,
                hoursDept,
                hoursFournisseur,
                hoursFamille,
                remboursementByType,
                totalFormationHours,
                totalSessions,
                realiseeKpi,
                planifieeKpi,
                autresKpi
        );
    }

    @Override
    public VisibiliteKpiDto getVisibiliteKpis(Integer clientId, Integer departementId, LocalDate start, LocalDate end) {
        List<SessionFormation> allSessions = sessionFormationRepository
                .findByStatutAndDateDebutBetweenAndEntreprise(
                        SessionFormationStatut.PLANIFIEE, start, end, clientId);

        List<SessionFormation> sessions = allSessions.stream()
                .filter(s -> sessionTouchesDepartement(s, departementId))
                .toList();

        long nbSessions = sessions.size();
        long nbZero = sessions.stream()
                .filter(s -> countDeptParticipants(s, departementId) == 0)
                .count();
        double moyenne = nbSessions > 0
                ? sessions.stream()
                .mapToLong(s -> countDeptParticipants(s, departementId))
                .average().orElse(0)
                : 0;
        moyenne = Math.round(moyenne * 10.0) / 10.0;

        return new VisibiliteKpiDto(nbSessions, moyenne, nbZero);
    }

    @Override
    public List<VisibiliteSessionDto> getVisibiliteSessions(Integer clientId, Integer departementId, LocalDate start, LocalDate end) {
        return sessionFormationRepository
                .findByStatutAndDateDebutBetweenAndEntreprise(
                        SessionFormationStatut.PLANIFIEE, start, end, clientId)
                .stream()
                .filter(s -> sessionTouchesDepartement(s, departementId))
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
                        (int) countDeptParticipants(s, departementId),
                        s.resolveJours()
                ))
                .toList();
    }

    @Override
    public List<VisibiliteSessionDto> getPlanifiedSessionsForCalendar(Integer clientId, Integer departementId, LocalDate start, LocalDate end) {
        return sessionFormationRepository
                .findByStatutOverlappingRangeAndEntreprise(SessionFormationStatut.PLANIFIEE, start, end, clientId)
                .stream()
                .filter(s -> sessionTouchesDepartement(s, departementId))
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
                        (int) countDeptParticipants(s, departementId),
                        s.resolveJours()
                ))
                .toList();
    }

    private Integer[] resolveYears(Integer clientId, Integer departementId, Integer[] years) {
        if (years != null && years.length > 0) return years;
        List<Integer> allYears = formationRepo.findDistinctYearsByClientId(clientId, departementId);
        return allYears.toArray(new Integer[0]);
    }

    // ─── Mapping helpers (unchanged) ────────────────────────────────────────────

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
                .map(p -> new ClientParticipantsByDepartmentKpiDto(p.getDepartementId(), p.getDepartement(), p.getNbParticipants()))
                .toList();
    }

    private List<ClientHoursByDepartmentKpiDto> mapHoursByDepartment(
            List<ClientHoursByDepartmentKpiProjection> projections) {
        return projections.stream()
                .map(p -> new ClientHoursByDepartmentKpiDto(p.getDepartementId(), p.getDepartement(), p.getTotalHeures()))
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
    public List<Integer> getAvailableYears(Integer clientId, Integer departementId) {
        return formationRepo.findDistinctYearsByClientId(clientId, departementId);
    }

    // ─── Growth KPI ──────────────────────────────────────────────────────────────

    @Override
    public TotalGrowthKpiDto getTotalGrowthKpi(
            Integer entrepriseId,
            Integer departementId,
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
                rawData = formationRepo.getTotalGrowthByDayForEntreprise(entrepriseId, departementId, month);
            }

            case "yearly" -> {
                if (hasYears) {
                    rawData = formationRepo.getTotalGrowthByYearForEntrepriseAndYears(entrepriseId, departementId, years);
                } else {
                    rawData = formationRepo.getTotalGrowthByYearForEntreprise(entrepriseId, departementId);
                }
            }

            case "monthly" -> {
                if (!hasYears) {
                    rawData = formationRepo.getTotalGrowthByMonthForEntreprise(entrepriseId, departementId);
                } else if (years.length == 1) {
                    rawData = formationRepo.getTotalGrowthByMonthForEntrepriseAndYear(entrepriseId, departementId, years[0]);
                } else {
                    rawData = formationRepo.getTotalGrowthByMonthForEntrepriseAndYears(entrepriseId, departementId, years);
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
