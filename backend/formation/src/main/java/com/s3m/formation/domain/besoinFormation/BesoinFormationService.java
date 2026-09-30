package com.s3m.formation.domain.besoinFormation;

import com.s3m.formation.api.dto.BesoinFormationRequest;
import com.s3m.formation.api.dto.BesoinFormationResponseDto;
import com.s3m.formation.api.dto.BesoinDecisionRequest;
import com.s3m.formation.auth.model.User;
import com.s3m.formation.auth.repository.UserRepository;
import com.s3m.formation.domain.departement.Departement;
import com.s3m.formation.domain.departement.DepartementRepository;
import com.s3m.formation.domain.entreprise.Entreprise;
import com.s3m.formation.domain.entreprise.EntrepriseRepository;
import com.s3m.formation.security.util.SecurityContextUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BesoinFormationService {

    private final BesoinFormationRepository besoinFormationRepository;
    private final EntrepriseRepository entrepriseRepository;
    private final DepartementRepository departementRepository;
    private final UserRepository userRepository;

    // =========================
    // GET ALL (SCOPED, ADMIN CAN FILTER)
    // =========================
    public List<BesoinFormationResponseDto> getVisibleBesoinsForCurrentUser(Integer requestedEntrepriseId) {
        if (currentUserIsAdminOnly()) {
            // ADMIN: requestedEntrepriseId == null -> every besoin in the DB
            List<BesoinFormation> besoins = (requestedEntrepriseId == null)
                    ? besoinFormationRepository.findAll()
                    : besoinFormationRepository.findByEntreprise_IdEntreprise(requestedEntrepriseId);
            return sortNewestFirst(besoins).stream().map(this::toDto).toList();
        }

        if (currentUserIsDepartmentChef()) {
            Integer entrepriseId = SecurityContextUtils.getEntrepriseId();
            Integer departementId = requireCurrentDepartment();
            return besoinFormationRepository.findByEntreprise_IdEntrepriseAndDepartement_Id(entrepriseId, departementId)
                    .stream().sorted(java.util.Comparator.comparing(BesoinFormation::getDateCreation,
                            java.util.Comparator.nullsLast(java.util.Comparator.reverseOrder())))
                    .map(this::toDto).toList();
        }

        // Everyone else: always scoped to their own entreprise, ignore requestedEntrepriseId
        Integer entrepriseId = SecurityContextUtils.getEntrepriseId();
        if (entrepriseId == null) return List.of();

        return besoinFormationRepository.findByEntreprise_IdEntreprise(entrepriseId)
                .stream()
                .sorted(java.util.Comparator.comparing(BesoinFormation::getDateCreation,
                        java.util.Comparator.nullsLast(java.util.Comparator.reverseOrder())))
                .map(this::toDto)
                .toList();
    }

    private List<BesoinFormation> sortNewestFirst(List<BesoinFormation> besoins) {
        return besoins.stream()
                .sorted(java.util.Comparator.comparing(BesoinFormation::getDateCreation,
                        java.util.Comparator.nullsLast(java.util.Comparator.reverseOrder())))
                .toList();
    }

    // =========================
    // GET BY ID (SECURITY CHECK — ADMIN BYPASSES)
    // =========================
    public BesoinFormationResponseDto getBesoinById(Integer id) {
        BesoinFormation besoin = besoinFormationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Besoin de formation non trouvé"));

        if (!currentUserIsAdminOnly()) {
            Integer entrepriseId = SecurityContextUtils.getEntrepriseId();
            if (besoin.getEntreprise() == null || !besoin.getEntreprise().getIdEntreprise().equals(entrepriseId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès refusé");
            }
            if (currentUserIsDepartmentChef() && (besoin.getDepartement() == null
                    || !requireCurrentDepartment().equals(besoin.getDepartement().getId()))) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès refusé");
            }
        }

        return toDto(besoin);
    }

    // =========================
    // CREATE (ADMIN only — also enforced via @PreAuthorize on the controller)
    // =========================
    public BesoinFormationResponseDto createBesoin(BesoinFormationRequest request) {
        if (!currentUserIsDepartmentChef() && request.idEntreprise() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Entreprise obligatoire");
        }
        if (request.intitule() == null || request.intitule().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "L'intitulé du besoin est obligatoire");
        }

        boolean chef = currentUserIsDepartmentChef();
        Integer entrepriseId = chef ? SecurityContextUtils.getEntrepriseId() : request.idEntreprise();
        if (entrepriseId == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Entreprise obligatoire");
        Entreprise entreprise = entrepriseRepository.findById(entrepriseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Entreprise non trouvée"));

        Departement departement = null;
        User requester = null;
        if (chef) {
            Integer departmentId = requireCurrentDepartment();
            departement = departementRepository.findById(departmentId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Département introuvable"));
            if (!entreprise.getIdEntreprise().equals(departement.getEntreprise().getIdEntreprise())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Le département ne correspond pas à l'entreprise du compte.");
            }
            requester = userRepository.findByEmail(SecurityContextUtils.getEmail())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Compte utilisateur introuvable"));
        }

        BesoinFormation besoin = BesoinFormation.builder()
                .entreprise(entreprise)
                .departement(departement)
                .requestedBy(requester)
                .status(chef ? "PENDING" : "APPROVED")
                .dept(chef ? departement.getNom() : request.dept())
                .intitule(request.intitule())
                .populationCible(request.populationCible())
                .nbCadre(request.nbCadre())
                .nbTam(request.nbTam())
                .nbPro(request.nbPro())
                .priorite(request.priorite())
                .periode(request.periode())
                .objectifs(request.objectifs())
                .competencesCiblees(request.competencesCiblees())
                .indicateursSucces(request.indicateursSucces())
                .evaluation(request.evaluation())
                .budgetEstimatif(request.budgetEstimatif())
                .remarques(request.remarques())
                .build();

        return toDto(besoinFormationRepository.save(besoin));
    }

    public BesoinFormationResponseDto decideBesoin(Integer id, BesoinDecisionRequest decision) {
        BesoinFormation besoin = besoinFormationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Besoin de formation non trouvé"));
        if (!"PENDING".equals(besoin.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Seules les demandes en attente peuvent être traitées.");
        }
        String status = decision.status() == null ? "" : decision.status().trim().toUpperCase();
        if (!List.of("APPROVED", "REJECTED").contains(status)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Décision invalide.");
        }
        String reason = decision.rejectionReason() == null ? "" : decision.rejectionReason().trim();
        if ("REJECTED".equals(status) && reason.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Veuillez saisir un motif de refus.");
        }
        besoin.setStatus(status);
        besoin.setRejectionReason("REJECTED".equals(status) ? reason : null);
        return toDto(besoinFormationRepository.save(besoin));
    }

    // =========================
    // UPDATE (ADMIN only)
    // =========================
    public BesoinFormationResponseDto updateBesoin(Integer id, BesoinFormationRequest request) {
        BesoinFormation existing = besoinFormationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Besoin de formation non trouvé"));

        if (request.idEntreprise() != null) {
            Entreprise entreprise = entrepriseRepository.findById(request.idEntreprise())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Entreprise non trouvée"));
            existing.setEntreprise(entreprise);
        }

        if (request.dept() != null)              existing.setDept(request.dept());
        if (request.intitule() != null)           existing.setIntitule(request.intitule());
        if (request.populationCible() != null)    existing.setPopulationCible(request.populationCible());
        if (request.nbCadre() != null)            existing.setNbCadre(request.nbCadre());
        if (request.nbTam() != null)              existing.setNbTam(request.nbTam());
        if (request.nbPro() != null)              existing.setNbPro(request.nbPro());
        if (request.priorite() != null)           existing.setPriorite(request.priorite());
        if (request.periode() != null)            existing.setPeriode(request.periode());
        if (request.objectifs() != null)          existing.setObjectifs(request.objectifs());
        if (request.competencesCiblees() != null) existing.setCompetencesCiblees(request.competencesCiblees());
        if (request.indicateursSucces() != null)  existing.setIndicateursSucces(request.indicateursSucces());
        if (request.evaluation() != null)         existing.setEvaluation(request.evaluation());
        if (request.budgetEstimatif() != null)    existing.setBudgetEstimatif(request.budgetEstimatif());
        if (request.remarques() != null)          existing.setRemarques(request.remarques());

        return toDto(besoinFormationRepository.save(existing));
    }

    // =========================
    // DELETE (ADMIN only)
    // =========================
    public void deleteBesoin(Integer id) {
        if (!besoinFormationRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Besoin de formation non trouvé");
        }
        besoinFormationRepository.deleteById(id);
    }

    // =========================
    // MAPPER
    // =========================
    private BesoinFormationResponseDto toDto(BesoinFormation b) {
        return new BesoinFormationResponseDto(
                b.getIdBesoin(),
                b.getEntreprise() != null ? b.getEntreprise().getIdEntreprise() : null,
                b.getEntreprise() != null ? b.getEntreprise().getNomEntreprise() : null,
                b.getDept(),
                b.getIntitule(),
                b.getPopulationCible(),
                b.getNbCadre(),
                b.getNbTam(),
                b.getNbPro(),
                b.getPriorite(),
                b.getPeriode(),
                b.getObjectifs(),
                b.getCompetencesCiblees(),
                b.getIndicateursSucces(),
                b.getEvaluation(),
                b.getBudgetEstimatif(),
                b.getRemarques(),
                b.getDateCreation(),
                b.getDateModification(),
                b.getDepartement() != null ? b.getDepartement().getId() : null,
                b.getRequestedBy() != null ? b.getRequestedBy().getPrenom() + " " + b.getRequestedBy().getNom() : null,
                b.getStatus(),
                b.getRejectionReason()
        );
    }

    // =========================
    // ROLE HELPER
    // =========================
    private boolean currentUserIsAdminOnly() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;

        for (GrantedAuthority authority : auth.getAuthorities()) {
            if ("ADMIN".equals(authority.getAuthority())) {
                return true;
            }
        }
        return false;
    }

    private boolean currentUserIsDepartmentChef() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> "CHEF_DEPARTEMENT".equals(a.getAuthority()));
    }

    private Integer requireCurrentDepartment() {
        Integer id = SecurityContextUtils.getDepartementId();
        if (id == null) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Aucun département n'est associé à ce compte.");
        return id;
    }
}
