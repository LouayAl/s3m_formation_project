package com.s3m.formation.domain.participation;


import com.s3m.formation.api.dto.ParticipantResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParticipationService {

    private final ParticipationRepository repository;

    public List<ParticipantResponseDto> getParticipantsBySession(Integer sessionId) {
        return repository.findBySession_IdSession(sessionId)
                .stream()
                .map(p -> new ParticipantResponseDto(
                        p.getEmploye().getIdEmploye(),
                        p.getEmploye().getNom(),
                        p.getEmploye().getPrenom(),
                        p.getEmploye().getEmail(),
                        p.getEmploye().getTelephone()
                ))
                .toList();
    }

    public long countParticipants(Integer sessionId) {
        return repository.countBySession_IdSession(sessionId);
    }
}
