package com.s3m.formation.domain.formateur;

import com.s3m.formation.domain.entreprise.Entreprise;
import com.s3m.formation.domain.entreprise.EntrepriseRepository;
import com.s3m.formation.domain.sessionFormation.SessionFormationRepository;
import com.s3m.formation.security.util.SecurityContextUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FormateurService {

    private final FormateurRepository formateurRepository;
    private final EntrepriseRepository entrepriseRepository;
    private final SessionFormationRepository sessionFormationRepository;

    // ── Existing methods, unchanged — still used wherever an active-only list is needed ──
    public List<Formateur> getAllActiveFormateurs() {
        if (canViewAllEntreprises()) return formateurRepository.findByActifTrue();
        return getAllVisibleFormateurs().stream()
                .filter(formateur -> Boolean.TRUE.equals(formateur.getActif()))
                .toList();
    }

    public List<Formateur> getAllVisibleFormateurs() {
        if (canViewAllEntreprises()) return formateurRepository.findAll();
        Integer entrepriseId = SecurityContextUtils.getEntrepriseId();
        if (entrepriseId == null) return List.of();
        return formateurRepository.findByEntreprise_IdEntreprise(entrepriseId);
    }

    public List<Formateur> getFormateursByEntreprise(Integer entrepriseId) {
        ensureEntrepriseAccess(entrepriseId);
        return formateurRepository.findByEntreprise_IdEntreprise(entrepriseId);
    }

    public Formateur getFormateurById(Integer id) {
        Formateur formateur = formateurRepository.findById(id).orElse(null);
        if (formateur != null && formateur.getEntreprise() != null) {
            ensureEntrepriseAccess(formateur.getEntreprise().getIdEntreprise());
        }
        return formateur;
    }

    // ── CRUD for the admin management page ──────────────────────────────────

    @Transactional(readOnly = true)
    public List<FormateurResponseDto> getAllFormateursForManagement() {
        return getAllVisibleFormateurs().stream()
                .map(this::toDto)
                .toList();
    }


    @Transactional(readOnly = true)
    public List<FormateurResponseDto> getAllActiveFormateursDto() {
        return getAllActiveFormateurs().stream().map(this::toDto).toList();
    }

    private boolean canViewAllEntreprises() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(authority -> "ADMIN".equals(authority.getAuthority())
                        || "ADMIN_FINANCE".equals(authority.getAuthority()));
    }

    private void ensureEntrepriseAccess(Integer entrepriseId) {
        if (!canViewAllEntreprises()
                && !java.util.Objects.equals(entrepriseId, SecurityContextUtils.getEntrepriseId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Vous ne pouvez consulter que les formateurs de votre entreprise.");
        }
    }

    @Transactional
    public FormateurResponseDto createFormateur(FormateurRequest request) {
        Entreprise entreprise = entrepriseRepository.findById(request.entrepriseId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Entreprise introuvable"));

        Formateur formateur = new Formateur();
        formateur.setNom(request.nom());
        formateur.setPrenom(request.prenom());
        formateur.setEmail(blankToNull(request.email()));
        formateur.setTelephone(request.telephone());
        formateur.setActif(request.actif() != null ? request.actif() : true);
        formateur.setEntreprise(entreprise);

        return toDto(formateurRepository.save(formateur));
    }

    @Transactional
    public FormateurResponseDto updateFormateur(Integer id, FormateurRequest request) {
        Formateur formateur = formateurRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Formateur introuvable"));

        Entreprise entreprise = entrepriseRepository.findById(request.entrepriseId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Entreprise introuvable"));

        formateur.setNom(request.nom());
        formateur.setPrenom(request.prenom());
        formateur.setEmail(blankToNull(request.email()));
        formateur.setTelephone(request.telephone());
        formateur.setActif(request.actif() != null ? request.actif() : formateur.getActif());
        formateur.setEntreprise(entreprise);

        return toDto(formateurRepository.save(formateur));
    }

    @Transactional
    public void deleteFormateur(Integer id) {
        if (!formateurRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Formateur introuvable");
        }
        if (sessionFormationRepository.existsByFormateur_IdFormateur(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Impossible de supprimer ce formateur : il est assigné à une ou plusieurs sessions. "
                            + "Désactivez-le plutôt que de le supprimer.");
        }
        formateurRepository.deleteById(id);
    }

    private FormateurResponseDto toDto(Formateur f) {
        return new FormateurResponseDto(
                f.getIdFormateur(),
                f.getNom(),
                f.getPrenom(),
                f.getEmail(),
                f.getTelephone(),
                f.getActif(),
                f.getEntreprise() != null ? f.getEntreprise().getIdEntreprise() : null,
                f.getEntreprise() != null ? f.getEntreprise().getNomEntreprise() : null
        );
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value;
    }
}
