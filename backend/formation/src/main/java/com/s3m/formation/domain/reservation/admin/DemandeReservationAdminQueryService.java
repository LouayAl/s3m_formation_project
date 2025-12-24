package com.s3m.formation.domain.reservation.admin;


import com.s3m.formation.api.dto.DemandeReservationAdminDto;
import com.s3m.formation.domain.reservation.DemandeReservation;
import com.s3m.formation.domain.reservation.DemandeReservationRepository;
import com.s3m.formation.domain.reservation.DemandeReservationStatut;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DemandeReservationAdminQueryService {
    private final DemandeReservationRepository repository;

    public List<DemandeReservationAdminDto> findAll() {
        return repository.findAllByOrderByDateCreationDesc()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<DemandeReservationAdminDto> findByStatut(DemandeReservationStatut statut) {
        return repository.findByStatutOrderByDateCreationDesc(statut)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public DemandeReservationAdminDto findOne(Integer id) {
        return repository.findById(id)
                .map(this::toDto)
                .orElseThrow(() ->
                        new IllegalArgumentException("Demande not found"));
    }

    private DemandeReservationAdminDto toDto(DemandeReservation d) {
        return new DemandeReservationAdminDto(
                d.getIdDemande(),
                d.getEntreprise().getNomEntreprise(),
                d.getFormation().getModule(),
                d.getStatut(),
                d.getDateDebutSouhaitee(),
                d.getDateFinSouhaitee(),
                d.getDateCreation(),
                d.getSessionFormation() != null
                        ? d.getSessionFormation().getIdSession()
                        : null
        );
    }
}
