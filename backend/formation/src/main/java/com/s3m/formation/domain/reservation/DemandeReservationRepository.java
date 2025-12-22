package com.s3m.formation.domain.reservation;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DemandeReservationRepository
        extends JpaRepository<DemandeReservation, Integer> {

    List<DemandeReservation> findByEntreprise_IdEntreprise(Integer idEntreprise);

    List<DemandeReservation> findByStatut(String statut);
}
