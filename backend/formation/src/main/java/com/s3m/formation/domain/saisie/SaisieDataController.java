package com.s3m.formation.domain.saisie;


import com.s3m.formation.api.dto.EmployeResponseDto;
import com.s3m.formation.api.dto.FormationResponseDto;
import com.s3m.formation.domain.employe.EmployeService;
import com.s3m.formation.domain.entreprise.Entreprise;
import com.s3m.formation.domain.entreprise.EntrepriseRepository;
import com.s3m.formation.domain.formateur.Formateur;
import com.s3m.formation.domain.formateur.FormateurRepository;
import com.s3m.formation.domain.formation.FormationService;
import lombok.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/saisie")
@RequiredArgsConstructor
public class SaisieDataController {

    private final FormationService formationService;
    private final EntrepriseRepository entrepriseRepository;
    private final FormateurRepository formateurRepository;
    private final EmployeService employeService;

    @GetMapping("/formations")
    public List<FormationResponseDto> getAllFormations() {
        return formationService.getAllFormations();
    }

    @GetMapping("/employes")
    public List<EmployeResponseDto> getAllEmployes() {
        return employeService.getAllEmployes();
    }

    @GetMapping("/entreprises")
    public List<Entreprise> getAllEntreprises() {
        return entrepriseRepository.findAll();
    }

    @GetMapping("/formateurs")
    public List<Formateur> getAllFormateurs() {
        return formateurRepository.findAll();
    }

    @GetMapping("/formateurs/by-entreprise/{id}")
    public List<Formateur> getFormateursByEntreprise(@PathVariable("id") Integer entrepriseId) {
        return formateurRepository.findByEntreprise_IdEntreprise(entrepriseId);
    }
}
