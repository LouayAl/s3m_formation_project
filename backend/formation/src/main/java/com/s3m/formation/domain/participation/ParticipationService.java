package com.s3m.formation.domain.participation;


import com.s3m.formation.api.dto.ParticipantResponseDto;
import com.s3m.formation.domain.employe.Employe;
import com.s3m.formation.domain.employe.EmployeRepository;
import com.s3m.formation.domain.sessionFormation.SessionFormation;
import com.s3m.formation.domain.sessionFormation.SessionFormationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class ParticipationService {

    private final ParticipationRepository participationRepository;
    private final SessionFormationRepository sessionRepository;
    private final EmployeRepository employeRepository;

    public List<ParticipantResponseDto> getParticipantsBySession(Integer sessionId) {
        return participationRepository.findBySession_IdSession(sessionId)
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
        return participationRepository.countBySession_IdSession(sessionId);
    }

    public void addParticipants(Integer sessionId, List<Integer> employeIds) {
        SessionFormation session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        for (Integer empId : employeIds) {
            Employe employe = employeRepository.findById(empId)
                    .orElseThrow(() -> new RuntimeException("Employe not found"));

            if (!participationRepository.existsBySession_IdSessionAndEmploye_IdEmploye(sessionId, empId)) {
                Participation participation = new Participation();
                participation.setSession(session);
                participation.setEmploye(employe);
                participationRepository.save(participation);
            }
        }
    }
}
