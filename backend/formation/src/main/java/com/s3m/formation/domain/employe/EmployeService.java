package com.s3m.formation.domain.employe;

import com.s3m.formation.api.dto.EmployeResponseDto;
import com.s3m.formation.domain.departement.Departement;
import com.s3m.formation.domain.departement.DepartementRepository;
import com.s3m.formation.domain.entreprise.Entreprise;
import com.s3m.formation.domain.entreprise.EntrepriseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class EmployeService {

    private final EmployeRepository employeRepository;
    private final EntrepriseRepository entrepriseRepository;
    private final DepartementRepository departementRepository;

    // ✅ Get all employes
    public List<EmployeResponseDto> getAllEmployes() {
        return employeRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    // ✅ Get employe by ID
    public EmployeResponseDto getEmployeById(Integer id) {
        Employe employe = employeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employé non trouvé"));
        return toDto(employe);
    }

    // ✅ Search employes
    public List<EmployeResponseDto> searchEmployes(String keyword) {
        return employeRepository.search(keyword)
                .stream()
                .map(this::toDto)
                .toList();
    }

    // ✅ Create employe
    public EmployeResponseDto createEmploye(Employe employe) {
        // Ensure entreprise exists
        if (employe.getEntreprise() == null || employe.getEntreprise().getIdEntreprise() == null) {
            throw new RuntimeException("Entreprise est obligatoire");
        }
        Entreprise entreprise = entrepriseRepository.findById(employe.getEntreprise().getIdEntreprise())
                .orElseThrow(() -> new RuntimeException("Entreprise non trouvée"));
        employe.setEntreprise(entreprise);

        // Optional: handle departement
        if (employe.getDepartement() != null && employe.getDepartement().getId() != null) {
            Departement departement = departementRepository.findById(employe.getDepartement().getId())
                    .orElseThrow(() -> new RuntimeException("Département non trouvé"));
            employe.setDepartement(departement);
        }

        Employe saved = employeRepository.save(employe);
        return toDto(saved);
    }

    // ✅ Update employe
    public EmployeResponseDto updateEmploye(Integer id, Employe updated) {
        Employe existing = employeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employé non trouvé"));

        existing.setNom(updated.getNom());
        existing.setPrenom(updated.getPrenom());
        existing.setEmail(updated.getEmail());
        existing.setTelephone(updated.getTelephone());
        existing.setCin(updated.getCin());
        existing.setMatricule(updated.getMatricule());
        existing.setCsp(updated.getCsp());
        existing.setFonction(updated.getFonction());
        existing.setTypeContrat(updated.getTypeContrat());
        existing.setF_h(updated.getF_h());
        existing.setDateEmbauche(updated.getDateEmbauche());
        existing.setDateNaissance(updated.getDateNaissance());

        // Update entreprise
        if (updated.getEntreprise() != null && updated.getEntreprise().getIdEntreprise() != null) {
            Entreprise entreprise = entrepriseRepository.findById(updated.getEntreprise().getIdEntreprise())
                    .orElseThrow(() -> new RuntimeException("Entreprise non trouvée"));
            existing.setEntreprise(entreprise);
        }

        // Update departement
        if (updated.getDepartement() != null && updated.getDepartement().getId() != null) {
            Departement departement = departementRepository.findById(updated.getDepartement().getId())
                    .orElseThrow(() -> new RuntimeException("Département non trouvé"));
            existing.setDepartement(departement);
        }

        Employe saved = employeRepository.save(existing);
        return toDto(saved);
    }

    // ✅ Delete employe
    public void deleteEmploye(Integer id) {
        employeRepository.deleteById(id);
    }

    // ✅ Map entity to DTO
    private EmployeResponseDto toDto(Employe employe) {
        return new EmployeResponseDto(
                employe.getIdEmploye(),
                employe.getNom(),
                employe.getPrenom(),
                employe.getEmail(),
                employe.getTelephone()
        );
    }
}
