package com.s3m.formation.domain.participation;

import com.s3m.formation.api.dto.ParticipantResponseDto;
import com.s3m.formation.domain.employe.Employe;
import com.s3m.formation.domain.employe.EmployeRepository;
import com.s3m.formation.domain.sessionFormation.SessionFormation;
import com.s3m.formation.domain.sessionFormation.SessionFormationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
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
                        p.getEmploye().getTelephone(),
                        p.getEmploye().getMatricule()
                ))
                .toList();
    }

    public long countParticipants(Integer sessionId) {
        return participationRepository.countBySession_IdSession(sessionId);
    }

    // Add multiple participants
    public void addParticipants(Integer sessionId, List<Integer> employeIds) {
        SessionFormation session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found"));

        for (Integer empId : employeIds) {
            Employe employe = employeRepository.findById(empId)
                    .orElseThrow(() -> new EntityNotFoundException("Employé not found: " + empId));

            if (!participationRepository.existsBySession_IdSessionAndEmploye_IdEmploye(sessionId, empId)) {
                Participation participation = new Participation();
                participation.setSession(session);
                participation.setEmploye(employe);
                participationRepository.save(participation);
            }
        }
    }

    public void deleteParticipant(Integer sessionId, Integer employeId) {
        Participation participation = participationRepository
                .findBySession_IdSessionAndEmploye_IdEmploye(sessionId, employeId)
                .orElseThrow(() -> new EntityNotFoundException("Participant not found"));

        participationRepository.delete(participation);
    }

    // Delete multiple participants
    public void deleteParticipants(Integer sessionId, List<Integer> employeIds) {
        for (Integer empId : employeIds) {
            participationRepository.findBySession_IdSessionAndEmploye_IdEmploye(sessionId, empId)
                    .ifPresent(participationRepository::delete);
        }
    }
}
