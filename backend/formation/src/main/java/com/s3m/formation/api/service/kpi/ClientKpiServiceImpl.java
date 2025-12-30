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

    private final ClientVolumeKpiRepository volumeRepo;
    private final ClientFinancierKpiRepository financierRepo;
    private final ClientIdentiteKpiRepository identiteRepo;
    private final ClientFormationKpiRepository formationRepo;
    private final ClientPopulationKpiRepository populationRepo;
    private final ClientEfficaciteKpiRepository efficaciteRepo;

    @Override
    public ClientKpiResponse getClientKpis(Integer clientId) {

        // 1️⃣ Identité
        ClientIdentiteKpiProjection identiteProjection = identiteRepo.computeIdentite(clientId);
        ClientIdentiteKpiDto identite = mapIdentite(identiteProjection, clientId);


        // 2️⃣ Volume
        ClientVolumeKpiProjection volumeProjection = volumeRepo.computeVolume(clientId);
        ClientVolumeKpiDto volume = mapVolume(volumeProjection);

        // 3️⃣ Financier
        ClientFinancierKpiProjection financierProjection = financierRepo.computeFinancier(clientId);
        ClientFinancierKpiDto financier = mapFinancier(financierProjection);


        // 4️⃣ Formations
        ClientFormationKpiProjection formationProjection = formationRepo.computeFormations(clientId);
        ClientFormationKpiDto formations = mapFormations(formationProjection);


        // 5️⃣ Population
        List<RepartitionItemProjection> cspProj = populationRepo.countByCsp(clientId);
        List<RepartitionItemProjection> fonctionProj = populationRepo.countByFonction(clientId);
        List<RepartitionItemProjection> typeContratProj = populationRepo.countByTypeContrat(clientId);
        List<RepartitionItemProjection> genreProj = populationRepo.countByGenre(clientId);

        ClientPopulationKpiDto population = new ClientPopulationKpiDto(
                mapRepartition(cspProj),
                mapRepartition(fonctionProj),
                mapRepartition(typeContratProj),
                mapRepartition(genreProj)
        );

        // 6️⃣ Efficacité
        ClientEfficaciteKpiProjection p = efficaciteRepo.computeEfficacite(clientId);
        ClientEfficaciteKpiDto efficacite = mapEfficacite(p);

        return new ClientKpiResponse(
                identite,
                volume,
                financier,
                formations,
                population,
                efficacite
        );
    }

    private ClientEfficaciteKpiDto mapEfficacite(ClientEfficaciteKpiProjection p) {
        if (p == null || p.getTotalEvals() == 0) {
            return new ClientEfficaciteKpiDto(BigDecimal.ZERO, BigDecimal.ZERO, null);
        }

        BigDecimal tauxMoyen = p.getEvalCount() != 0
                ? p.getSumTaux().divide(BigDecimal.valueOf(p.getEvalCount()), 2, BigDecimal.ROUND_HALF_UP)
                : BigDecimal.ZERO;

        BigDecimal pourcentageEvalue = BigDecimal.valueOf(p.getEvalCount() * 100.0 / p.getTotalEvals());

        return new ClientEfficaciteKpiDto(pourcentageEvalue, tauxMoyen, p.getLastEvalDate());
    }

    private List<RepartitionKpiItemDto> mapRepartition(List<RepartitionItemProjection> items) {
        return items.stream()
                .map(p -> new RepartitionKpiItemDto(p.getLabel(), p.getCount()))
                .toList();
    }

    private ClientFormationKpiDto mapFormations(ClientFormationKpiProjection p) {
        if (p == null) {
            return new ClientFormationKpiDto(0, null, null, BigDecimal.ZERO, BigDecimal.ZERO);
        }

        return new ClientFormationKpiDto(
                p.getTotalFormations().intValue(),
                p.getFormationLaPlusSuivie(),
                p.getFamillePrincipale(),
                p.getPourcentageInterne(),
                p.getPourcentageExterne()
        );
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

    private ClientVolumeKpiDto mapVolume(ClientVolumeKpiProjection p) {
        if (p == null) {
            return new ClientVolumeKpiDto(0, 0, BigDecimal.ZERO, BigDecimal.ZERO);
        }

        return new ClientVolumeKpiDto(
                p.getTotalSessions().intValue(),
                p.getTotalParticipants().intValue(),
                p.getTotalJours(),
                p.getTotalHeures()
        );
    }

    private ClientIdentiteKpiDto mapIdentite(ClientIdentiteKpiProjection p, Integer clientId) {
        if (p == null) {
            return new ClientIdentiteKpiDto(clientId, null, null, null, null, null);
        }

        return new ClientIdentiteKpiDto(
                clientId,
                p.getNomClient(),
                p.getPremiereAnnee(),
                p.getDerniereAnnee(),
                p.getDatePremiere(),
                p.getDateDerniere()
        );
    }

}