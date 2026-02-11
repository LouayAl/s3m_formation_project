package com.s3m.formation.domain.participation;

import com.s3m.formation.api.dto.ParticipantResponseDto;
import com.s3m.formation.domain.employe.Employe;
import com.s3m.formation.domain.employe.EmployeRepository;
import com.s3m.formation.domain.sessionFormation.SessionFormation;
import com.s3m.formation.domain.sessionFormation.SessionFormationRepository;
import com.s3m.formation.domain.sessionFormation.SessionFormationStatut;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
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
        checkParticipantModificationAllowed(session);
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
        SessionFormation session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found"));

        checkParticipantModificationAllowed(session);

        Participation participation = participationRepository
                .findBySession_IdSessionAndEmploye_IdEmploye(sessionId, employeId)
                .orElseThrow(() -> new EntityNotFoundException("Participant not found"));

        participationRepository.delete(participation);
    }

    // Delete multiple participants
    public void deleteParticipants(Integer sessionId, List<Integer> employeIds) {
        SessionFormation session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found"));

        checkParticipantModificationAllowed(session);

        for (Integer empId : employeIds) {
            participationRepository.findBySession_IdSessionAndEmploye_IdEmploye(sessionId, empId)
                    .ifPresent(participationRepository::delete);
        }
    }

    //Helper method for admin
    private boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));
    }

    private void checkParticipantModificationAllowed(SessionFormation session) {

        // ✅ ADMIN can always modify
        if (isAdmin()) return;

        // ❌ MANAGER locked once session started
        if (session.getStatut() != SessionFormationStatut.PLANIFIEE) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Managers cannot modify participants once the session has started"
            );
        }

        // Optional extra safety: also block if dateDebut is today/past
        if (!session.getDateDebut().isAfter(LocalDate.now())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Participants cannot be modified on or after the start date"
            );
        }
    }
}
