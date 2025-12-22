package com.s3m.formation.domain.sessionFormation;


import com.s3m.formation.api.dto.SessionResponseDto;
import lombok.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionFormationService {

    private final SessionFormationRepository repository;

    public List<SessionResponseDto> getSessionsByFormation(Integer formationId) {
        return repository.findByFormation_IdFormation(formationId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public SessionResponseDto getSession(Integer sessionId) {
        return repository.findById(sessionId)
                .map(this::toDto)
                .orElse(null);
    }

    private SessionResponseDto toDto(SessionFormation session) {
        return new SessionResponseDto(
                session.getIdSession(),
                session.getDateDebut(),
                session.getDateFin(),
                session.getStatut(),
                session.getFormateur() != null
                        ? session.getFormateur().getNom() + " " + session.getFormateur().getPrenom()
                        : null,
                session.getFournisseur() != null
                        ? session.getFournisseur().getNomEntreprise()
                        : null
        );
    }
}
