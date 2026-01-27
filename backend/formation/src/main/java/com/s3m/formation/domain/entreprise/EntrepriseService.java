package com.s3m.formation.domain.entreprise;

import com.s3m.formation.api.dto.EntrepriseResponseDto;
import com.s3m.formation.domain.employe.EmployeRepository;
import com.s3m.formation.domain.sessionFormation.SessionFormationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class EntrepriseService {
    @Autowired
    private final EntrepriseRepository entrepriseRepository;

    @Autowired
    private final EmployeRepository employeRepository;

    @Autowired
    private final SessionFormationRepository sessionFormationRepository;

    // Get all entreprises
    public List<EntrepriseResponseDto> getAllEntreprises() {
        return entrepriseRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    // Get entreprise by ID
    public EntrepriseResponseDto getEntrepriseById(Integer id) {
        Entreprise e = entrepriseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entreprise not found"));
        return toDto(e);
    }

    // Search entreprises by name
    public List<EntrepriseResponseDto> searchEntreprises(String keyword) {
        return entrepriseRepository.search(keyword)
                .stream()
                .map(this::toDto)
                .toList();
    }

    // Create entreprise
    public EntrepriseResponseDto createEntreprise(Entreprise entreprise) {
        Entreprise saved = entrepriseRepository.save(entreprise);
        return toDto(saved);
    }

    // Update entreprise
    public EntrepriseResponseDto updateEntreprise(Integer id, Entreprise updated) {
        Entreprise existing = entrepriseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entreprise not found"));

        existing.setNomEntreprise(updated.getNomEntreprise());
        Entreprise saved = entrepriseRepository.save(existing);
        return toDto(saved);
    }

    // Delete entreprise
    public void deleteEntreprise(Integer id) {
        // Check if entreprise exists first
        Entreprise entreprise = entrepriseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Entreprise non trouvée"));
        // Check if there are employees linked
        boolean hasEmployees = employeRepository.existsByEntreprise_IdEntreprise(id);
        if (hasEmployees) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Impossible de supprimer cette entreprise : elle possède des employés assignés."
            );
        }
        // Optional: check if there are sessions linked
        boolean hasSessions = sessionFormationRepository.existsByEntreprise_IdEntreprise(id);
        if (hasSessions) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Impossible de supprimer cette entreprise : elle possède des sessions de formation assignées."
            );
        }
        entrepriseRepository.deleteById(id);
    }

    // Convert entity to DTO
    private EntrepriseResponseDto toDto(Entreprise entreprise) {
        return new EntrepriseResponseDto(
                entreprise.getIdEntreprise(),
                entreprise.getNomEntreprise()
        );
    }
}
