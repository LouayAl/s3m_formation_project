package com.s3m.formation.domain.sessionFormation;

import com.s3m.formation.domain.reservation.DemandeReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessionFormationRepository
        extends JpaRepository<SessionFormation, Integer> {
    List<SessionFormation> findByFormation_IdFormation(Integer idFormation);
    boolean existsByDemande(DemandeReservation demande);

}
