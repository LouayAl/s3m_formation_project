package com.s3m.formation.domain.formateur;

import lombok.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/formateurs")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // adjust to your frontend URL in production
public class FormateurController {

    private final FormateurService formateurService;

    // ✅ Get all active formateurs
    @GetMapping
    public List<Formateur> getAllFormateurs() {
        return formateurService.getAllActiveFormateurs();
    }

    // ✅ Get formateurs by entreprise
    @GetMapping("/entreprise/{id}")
    public List<Formateur> getFormateursByEntreprise(@PathVariable Integer id) {
        return formateurService.getFormateursByEntreprise(id);
    }

    // ✅ Get formateur by ID
    @GetMapping("/{id}")
    public Formateur getFormateurById(@PathVariable Integer id) {
        return formateurService.getFormateurById(id);
    }
}