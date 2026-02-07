package com.s3m.formation.api.service.kpi;

import com.s3m.formation.api.kpi.client.dto.*;
import com.s3m.formation.api.kpi.client.projection.*;
import com.s3m.formation.api.kpi.client.repository.*;
import lombok.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

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



    @Override
    public ClientKpiResponse getClientKpis(Integer clientId) {

        // 3️⃣ Financier
        ClientFinancierKpiProjection financierProjection = financierRepo.computeFinancier(clientId);
        ClientFinancierKpiDto financier = mapFinancier(financierProjection);

        // 3️⃣a Financier par type de remboursement
        List<ClientFinancierByRemboursementProjection> remboursementProj =
                financierRepo.computeFinancierByRemboursement();
        List<ClientFinancierByRemboursementDto> remboursementByType = remboursementProj.stream()
                .map(p -> new ClientFinancierByRemboursementDto(
                        p.getRemboursement(),
                        BigDecimal.valueOf(p.getTotalHeures())
                ))
                .toList();

        // 5️⃣ Population
        List<RepartitionItemProjection> cspProj = populationRepo.countByCsp(clientId);
        List<RepartitionItemProjection> fonctionProj = populationRepo.countByFonction(clientId);
        List<RepartitionItemProjection> typeContratProj = populationRepo.countByTypeContrat(clientId);
        List<RepartitionItemProjection> genreProj = populationRepo.countByGenre(clientId);

        List<ClientGenderByDepartmentKpiProjection> genderDeptProj = populationRepo.getGenderByDepartmentForAllEntreprises();
        List<GenderHoursKpiProjection> genderHoursProj = populationRepo.getTrainingHoursByGender();
        List<CspHoursKpiProjection> cspHoursProj = populationRepo.getTrainingHoursByCsp();

        // total participants
        TotalParticipantsKpiProjection participantsProj = populationRepo.getTotalParticipants();
        Long totalParticipants = participantsProj != null ? participantsProj.getTotalParticipants() : 0L;


        List<EmployeGenderByDepartmentKpiDto> genderByDepartment = genderDeptProj.stream()
                .map(p -> new EmployeGenderByDepartmentKpiDto(
                        p.getDepartement(),
                        p.getGenre(),
                        p.getNombre()
                ))
                .toList();

        List<GenderHoursKpiDto> genderHours = genderHoursProj.stream()
                .map(p -> new GenderHoursKpiDto(
                        p.getLabel(),
                        p.getTotalHeures(),
                        p.getNombreEmployes()
                ))
                .toList();

        List<CspHoursKpiDto> cspHours = cspHoursProj.stream()
                .map(p -> new CspHoursKpiDto(
                        p.getCsp(),
                        p.getTotalHeures(),
                        p.getNombreEmployes()
                ))
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


        // 7️⃣ Other KPIs
        List<ClientParticipantsByDepartmentKpiProjection> participantsDeptProj = participantsDeptRepo.findByClientId(clientId);
        List<ClientHoursByDepartmentKpiProjection> hoursDeptProj = hoursDeptRepo.findByClientId(clientId);
        List<ClientHoursByFournisseurKpiProjection> hoursFournisseurProj = hoursFournisseurRepo.findByClientId(clientId);
        List<ClientHoursByFamilleFormationKpiProjection> hoursFamilleProj = hoursFamilleRepo.findByClientId(clientId);

        List<ClientParticipantsByDepartmentKpiDto> participantsDept = mapParticipantsByDepartment(participantsDeptProj);
        List<ClientHoursByDepartmentKpiDto> hoursDept = mapHoursByDepartment(hoursDeptProj);
        List<ClientHoursByFournisseurKpiDto> hoursFournisseur = mapHoursByFournisseur(hoursFournisseurProj);
        List<ClientHoursByFamilleFormationKpiDto> hoursFamille = mapHoursByFamilleFormation(hoursFamilleProj);

        // Total Formation Hours
        TotalFormationHoursProjection totalHoursProj = formationRepo.getTotalFormationHours();
        BigDecimal totalFormationHours = totalHoursProj != null ? totalHoursProj.getTotalHeures() : BigDecimal.ZERO;

        // Total sessions
        Long totalSessions = totalSessionsRepo.getTotalSessions().getTotalSessions();

        return new ClientKpiResponse(
                financier,
                population,
                participantsDept,
                hoursDept,
                hoursFournisseur,
                hoursFamille,
                remboursementByType, // <-- new field for pie chart
                totalFormationHours,
                totalSessions
        );
    }

    private List<RepartitionKpiItemDto> mapRepartition(List<RepartitionItemProjection> items) {
        return items.stream()
                .map(p -> new RepartitionKpiItemDto(p.getLabel(), p.getCount()))
                .toList();
    }

    private ClientFinancierKpiDto mapFinancier(ClientFinancierKpiProjection p) {
        if (p == null) {
            return new ClientFinancierKpiDto(
                    BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO
            );
        }

        BigDecimal coutMoyenParJour = p.getTotalJours().compareTo(BigDecimal.ZERO) > 0
                ? p.getCoutTotal().divide(p.getTotalJours(), 2, BigDecimal.ROUND_HALF_UP)
                : BigDecimal.ZERO;

        BigDecimal coutMoyenParParticipant = p.getTotalParticipants() > 0
                ? p.getCoutTotal().divide(BigDecimal.valueOf(p.getTotalParticipants()), 2, BigDecimal.ROUND_HALF_UP)
                : BigDecimal.ZERO;

        return new ClientFinancierKpiDto(
                p.getCoutTotal(),
                coutMoyenParJour,
                coutMoyenParParticipant,
                p.getCoutRembourse(),
                p.getCoutNonRembourse()
        );
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
}
