package com.s3m.formation.domain.sessionFormation.admin;


import com.s3m.formation.domain.sessionFormation.SessionFormation;
import com.s3m.formation.domain.sessionFormation.SessionFormationRepository;
import com.s3m.formation.domain.sessionFormation.SessionFormationStatut;
import com.s3m.formation.domain.sessionFormation.admin.dto.SessionFormationAdminListDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SessionFormationAdminQueryService {

    private final SessionFormationRepository repository;

    public List<SessionFormationAdminListDto> search(
            SessionFormationStatut statut,
            Integer formationId,
            Integer entrepriseId,
            LocalDate startDate,
            LocalDate endDate
    ) {
        List<SessionFormation> sessions = repository.adminSearch(
                statut,
                formationId,
                entrepriseId,
                startDate,
                endDate
        );

        System.out.println("ADMIN SESSIONS COUNT = " + sessions.size());
        return repository.adminSearch(
                        statut,
                        formationId,
                        entrepriseId,
                        startDate,
                        endDate
                )
                .stream()
                .map(this::toDto)
                .toList();
    }

    private SessionFormationAdminListDto toDto(SessionFormation s) {
        return new SessionFormationAdminListDto(
                s.getIdSession(),
                s.getFormation().getModule(),
                s.getDemande().getEntreprise().getNomEntreprise(),
                s.getStatut(),
                s.getDateDebut(),
                s.getDateFin(),
                s.getFormateur() != null
                        ? s.getFormateur().getNom() + " " + s.getFormateur().getPrenom()
                        : null,
                s.getFournisseur() != null
                        ? s.getFournisseur().getNomEntreprise()
                        : null
        );
    }
}
