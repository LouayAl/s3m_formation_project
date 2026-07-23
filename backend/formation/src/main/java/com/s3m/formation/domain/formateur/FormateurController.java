package com.s3m.formation.domain.formateur;

import lombok.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/formateurs")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FormateurController {

    private final FormateurService formateurService;

    // ── Existing — unchanged, active-only, no role gate ─────────────────────
    @GetMapping
    public List<Formateur> getAllFormateurs() {
        return formateurService.getAllActiveFormateurs();
    }

    @GetMapping("/entreprise/{id}")
    public List<Formateur> getFormateursByEntreprise(@PathVariable Integer id) {
        return formateurService.getFormateursByEntreprise(id);
    }

    @GetMapping("/{id}")
    public Formateur getFormateurById(@PathVariable Integer id) {
        return formateurService.getFormateurById(id);
    }

    // ── New — management page: full list (active + inactive), any authenticated user ──
    @GetMapping("/all")
    public List<FormateurResponseDto> getAllFormateursForManagement() {
        return formateurService.getAllFormateursForManagement();
    }

    // ── New — mutations, ADMIN only ──────────────────────────────────────────
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public FormateurResponseDto createFormateur(@RequestBody FormateurRequest request) {
        return formateurService.createFormateur(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public FormateurResponseDto updateFormateur(@PathVariable Integer id, @RequestBody FormateurRequest request) {
        return formateurService.updateFormateur(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteFormateur(@PathVariable Integer id) {
        formateurService.deleteFormateur(id);
    }
}