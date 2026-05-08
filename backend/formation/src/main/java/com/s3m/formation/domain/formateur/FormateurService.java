package com.s3m.formation.domain.formateur;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FormateurService {

    private final FormateurRepository formateurRepository;

    // Get all active formateurs
    public List<Formateur> getAllActiveFormateurs() {
        return formateurRepository.findByActifTrue();
    }

    // Get formateurs by entreprise ID
    public List<Formateur> getFormateursByEntreprise(Integer entrepriseId) {
        return formateurRepository.findByEntreprise_IdEntreprise(entrepriseId);
    }

    // Optional: get formateur by ID
    public Formateur getFormateurById(Integer id) {
        return formateurRepository.findById(id).orElse(null);
    }


}
