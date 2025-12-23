package com.s3m.formation.domain.reservation.admin;


import com.s3m.formation.domain.reservation.DemandeReservation;
import com.s3m.formation.domain.reservation.DemandeReservationRepository;
import com.s3m.formation.domain.reservation.DemandeReservationStatut;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DemandeReservationAdminQueryService {
    private final DemandeReservationRepository repository;

    public List<DemandeReservation> findAll() {
        return repository.findAll();
    }

    public List<DemandeReservation> findByStatut(DemandeReservationStatut statut) {
        return repository.findByStatut(statut);
    }

    public DemandeReservation findOne(Integer id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Demande not found"));
    }
}
