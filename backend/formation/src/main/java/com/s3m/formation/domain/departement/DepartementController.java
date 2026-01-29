package com.s3m.formation.domain.departement;

import com.s3m.formation.api.dto.DepartementResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departements")
@RequiredArgsConstructor
public class DepartementController {

    private final DepartementRepository departementRepository;

    // ✅ Get all departements
    @GetMapping
    public List<DepartementResponseDto> getAllDepartements() {
        return departementRepository.findAll()
                .stream()
                .map(d -> new DepartementResponseDto(
                        d.getId(),
                        d.getNom(),
                        d.getEntreprise().getIdEntreprise()
                ))
                .toList();
    }

    // ✅ Get departements by entreprise
    @GetMapping("/byEntreprise/{idEntreprise}")
    public List<DepartementResponseDto> getDepartementsByEntreprise(@PathVariable Integer idEntreprise) {
        return departementRepository.findByEntrepriseIdEntreprise(idEntreprise)
                .stream()
                .map(d -> new DepartementResponseDto(
                        d.getId(),
                        d.getNom(),
                        d.getEntreprise().getIdEntreprise()
                ))
                .toList();
    }

    // Optional: get single departement by ID
    @GetMapping("/{id}")
    public DepartementResponseDto getDepartementById(@PathVariable Integer id) {
        Departement d = departementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Département non trouvé"));
        return new DepartementResponseDto(
                d.getId(),
                d.getNom(),
                d.getEntreprise().getIdEntreprise()
        );
    }
}
