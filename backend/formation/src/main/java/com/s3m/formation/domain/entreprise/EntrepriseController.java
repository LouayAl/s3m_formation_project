package com.s3m.formation.domain.entreprise;

import com.s3m.formation.api.dto.EntrepriseResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/entreprises")
@RequiredArgsConstructor
public class EntrepriseController {

    private final EntrepriseService entrepriseService;

    @GetMapping
    public List<EntrepriseResponseDto> getAllEntreprises() {
        return entrepriseService.getAllEntreprises();
    }

    @GetMapping("/{id}")
    public EntrepriseResponseDto getEntrepriseById(@PathVariable Integer id) {
        return entrepriseService.getEntrepriseById(id);
    }

    @GetMapping("/search")
    public List<EntrepriseResponseDto> searchEntreprises(@RequestParam String keyword) {
        return entrepriseService.searchEntreprises(keyword);
    }

    @PostMapping
    public EntrepriseResponseDto createEntreprise(@RequestBody Entreprise entreprise) {
        return entrepriseService.createEntreprise(entreprise);
    }

    @PutMapping("/{id}")
    public EntrepriseResponseDto updateEntreprise(@PathVariable Integer id, @RequestBody Entreprise entreprise) {
        return entrepriseService.updateEntreprise(id, entreprise);
    }

    @DeleteMapping("/{id}")
    public void deleteEntreprise(@PathVariable Integer id) {
        entrepriseService.deleteEntreprise(id);
    }
}
