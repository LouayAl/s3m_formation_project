package com.s3m.formation.domain.participation;

import com.s3m.formation.api.dto.ParticipantResponseDto;
import com.s3m.formation.domain.employe.Employe;
import com.s3m.formation.domain.employe.EmployeRepository;
import com.s3m.formation.domain.sessionFormation.SessionFormation;
import com.s3m.formation.domain.sessionFormation.SessionFormationRepository;
import com.s3m.formation.domain.sessionFormation.SessionFormationStatut;
import com.s3m.formation.security.util.AuthDetails;
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
                .filter(p -> !isDepartmentChef() || belongsToCurrentDepartment(p.getEmploye()))
                .map(p -> new ParticipantResponseDto(
                        p.getEmploye().getIdEmploye(),
                        p.getEmploye().getNom(),
                        p.getEmploye().getPrenom(),
                        p.getEmploye().getEmail(),
                        p.getEmploye().getTelephone(),
                        p.getEmploye().getCin(),
                        p.getEmploye().getMatricule(),
                        p.getEmploye().getDepartement() != null ? p.getEmploye().getDepartement().getNom() : null
                ))
                .toList();
    }

    public long countParticipants(Integer sessionId) {
        if (isDepartmentChef()) {
            return participationRepository.findBySession_IdSession(sessionId).stream()
                    .filter(p -> belongsToCurrentDepartment(p.getEmploye()))
                    .count();
        }
        return participationRepository.countBySession_IdSession(sessionId);
    }

    // Add multiple participants
    public void addParticipants(Integer sessionId, List<Integer> employeIds) {
        SessionFormation session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found"));
        ensureVisibleToDepartmentChef(session);
        checkParticipantModificationAllowed(session);
        for (Integer empId : employeIds) {
            Employe employe = employeRepository.findById(empId)
                    .orElseThrow(() -> new EntityNotFoundException("Employé not found: " + empId));

            if (isDepartmentChef() && (!belongsToCurrentDepartment(employe)
                    || session.getEntreprise() == null || employe.getEntreprise() == null
                    || !session.getEntreprise().getIdEntreprise().equals(employe.getEntreprise().getIdEntreprise()))) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Vous pouvez uniquement ajouter vos employés du même département.");
            }
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
        ensureVisibleToDepartmentChef(session);

        checkParticipantModificationAllowed(session);

        Participation participation = participationRepository
                .findBySession_IdSessionAndEmploye_IdEmploye(sessionId, employeId)
                .orElseThrow(() -> new EntityNotFoundException("Participant not found"));
        ensureParticipantBelongsToDepartmentChef(participation);

        participationRepository.delete(participation);
    }

    // Delete multiple participants
    public void deleteParticipants(Integer sessionId, List<Integer> employeIds) {
        SessionFormation session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session not found"));
        ensureVisibleToDepartmentChef(session);

        checkParticipantModificationAllowed(session);

        for (Integer empId : employeIds) {
            participationRepository.findBySession_IdSessionAndEmploye_IdEmploye(sessionId, empId)
                    .ifPresent(participation -> {
                        ensureParticipantBelongsToDepartmentChef(participation);
                        participationRepository.delete(participation);
                    });
        }
    }

    //Helper method for admin
    private boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ADMIN")|| role.equals("EQUIPMENT_MANAGER"));
    }

    private boolean isDepartmentChef() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> "CHEF_DEPARTEMENT".equals(a.getAuthority()));
    }

    private boolean belongsToCurrentDepartment(Employe employe) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        AuthDetails details = auth != null && auth.getDetails() instanceof AuthDetails d ? d : null;
        return details != null && details.getDepartementId() != null && details.getEntrepriseId() != null
                && employe != null && employe.getDepartement() != null
                && details.getDepartementId().equals(employe.getDepartement().getId())
                && employe.getEntreprise() != null
                && details.getEntrepriseId().equals(employe.getEntreprise().getIdEntreprise());
    }

    private void ensureVisibleToDepartmentChef(SessionFormation session) {
        if (isDepartmentChef() && participationRepository.findBySession_IdSession(session.getIdSession()).stream()
                .noneMatch(p -> belongsToCurrentDepartment(p.getEmploye()))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Session non trouvée pour ce département.");
        }
    }

    private void ensureParticipantBelongsToDepartmentChef(Participation participation) {
        if (isDepartmentChef() && !belongsToCurrentDepartment(participation.getEmploye())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Vous pouvez uniquement gérer les employés de votre département.");
        }
    }

    private void checkParticipantModificationAllowed(SessionFormation session) {

        // ✅ ADMIN can always modify
        if (isAdmin() || isDepartmentChef()) return;

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
