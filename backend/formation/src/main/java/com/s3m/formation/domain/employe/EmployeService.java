package com.s3m.formation.domain.employe;

import com.s3m.formation.api.dto.EmployeResponseDto;
import com.s3m.formation.domain.departement.Departement;
import com.s3m.formation.domain.departement.DepartementRepository;
import com.s3m.formation.domain.entreprise.Entreprise;
import com.s3m.formation.domain.entreprise.EntrepriseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EmployeService {

    private final EmployeRepository employeRepository;
    private final EntrepriseRepository entrepriseRepository;
    private final DepartementRepository departementRepository;

    // =========================
    // GET
    // =========================

    public List<EmployeResponseDto> getAllEmployes() {
        return employeRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public EmployeResponseDto getEmployeById(Integer id) {
        Employe employe = employeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Employé non trouvé"));
        return toDto(employe);
    }

    public List<EmployeResponseDto> searchEmployes(String keyword) {
        return employeRepository.search(keyword)
                .stream()
                .map(this::toDto)
                .toList();
    }

    // =========================
    // CREATE
    // =========================

    public EmployeResponseDto createEmploye(Employe employe) {
        log.info("=== CREATE EMPLOYE START ===");
        log.info("Payload received: {}", employe);

        if (employe.getEntreprise() == null || employe.getEntreprise().getIdEntreprise() == null) {
            log.error("Entreprise is null in payload");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Entreprise obligatoire");
        }

        Entreprise entreprise = entrepriseRepository.findById(employe.getEntreprise().getIdEntreprise())
                .orElseThrow(() -> {
                    log.error("Entreprise not found with id={}", employe.getEntreprise().getIdEntreprise());
                    return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Entreprise non trouvée");
                });

        Departement departement = null;
        if (employe.getDepartement() != null && employe.getDepartement().getId() != null) {
            departement = departementRepository.findById(employe.getDepartement().getId())
                    .orElseThrow(() -> {
                        log.error("Departement not found with id={}", employe.getDepartement().getId());
                        return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Département non trouvé");
                    });
        }

        employe.setEntreprise(entreprise);
        employe.setDepartement(departement);

        try {
            log.info("Saving employee...");
            Employe saved = employeRepository.saveAndFlush(employe);
            log.info("Employee saved with ID={}", saved.getIdEmploye());
            log.info("=== CREATE EMPLOYE END SUCCESS ===");
            return toDto(saved);
        } catch (Exception e) {
            log.error("Error while saving employee", e);
            throw e;
        }
    }

    // =========================
    // UPDATE
    // =========================

    public EmployeResponseDto updateEmploye(Integer id, Employe updated) {

        Employe existing = employeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Employé non trouvé"));

        existing.setNom(updated.getNom());
        existing.setPrenom(updated.getPrenom());
        existing.setEmail(updated.getEmail());
        existing.setTelephone(updated.getTelephone());
        existing.setCin(updated.getCin());
        existing.setCnss(updated.getCnss());
        existing.setMatricule(updated.getMatricule());
        existing.setCsp(updated.getCsp());
        existing.setFonction(updated.getFonction());
        existing.setTypeContrat(updated.getTypeContrat());
        existing.setF_h(updated.getF_h());
        existing.setDateEmbauche(updated.getDateEmbauche());
        existing.setDateNaissance(updated.getDateNaissance());

        // Update entreprise if provided
        if (updated.getEntreprise() != null && updated.getEntreprise().getIdEntreprise() != null) {
            Entreprise entreprise = entrepriseRepository
                    .findById(updated.getEntreprise().getIdEntreprise())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "Entreprise non trouvée"));
            existing.setEntreprise(entreprise);
        }

        // Update departement (optional)
        if (updated.getDepartement() != null && updated.getDepartement().getId() != null) {
            Departement departement = departementRepository
                    .findById(updated.getDepartement().getId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "Département non trouvé"));
            existing.setDepartement(departement);
        } else {
            existing.setDepartement(null);
        }

        try {
            Employe saved = employeRepository.saveAndFlush(existing);
            return toDto(saved);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Matricule ou email déjà existant"
            );
        }
    }

    // =========================
    // DELETE
    // =========================

    public void deleteEmploye(Integer id) {
        if (!employeRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Employé non trouvé");
        }
        employeRepository.deleteById(id);
    }

    // =========================
    // DTO MAPPER
    // =========================

    private EmployeResponseDto toDto(Employe employe) {
        return new EmployeResponseDto(
                employe.getIdEmploye(),
                employe.getNom(),
                employe.getPrenom(),
                employe.getEmail(),
                employe.getTelephone(),
                employe.getCin(),
                employe.getCnss(),
                employe.getMatricule(),
                employe.getCsp(),
                employe.getFonction(),
                employe.getTypeContrat(),
                employe.getF_h(),
                employe.getDateEmbauche(),
                employe.getDateNaissance(),
                employe.getEntreprise() != null ? employe.getEntreprise().getIdEntreprise() : null,
                employe.getEntreprise() != null ? employe.getEntreprise().getNomEntreprise() : null,
                employe.getDepartement() != null ? employe.getDepartement().getId() : null,
                employe.getDepartement() != null ? employe.getDepartement().getNom() : null
        );
    }
}
