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
import java.util.LinkedHashSet;
import java.util.Set;

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
            return sortNewestFirst(besoins).stream()
                    .filter(besoin -> !"BROUILLON".equals(besoin.getStatus()))
                    .map(this::toDto).toList();
        }

        if (currentUserHasDepartmentScope()) {
            Integer entrepriseId = SecurityContextUtils.getEntrepriseId();
            Integer departementId = requireCurrentDepartment();
            return besoinFormationRepository.findByEntreprise_IdEntrepriseAndDepartement_Id(entrepriseId, departementId)
                    .stream()
                    .filter(this::isVisibleToCurrentRequester)
                    .sorted(java.util.Comparator.comparing(BesoinFormation::getDateCreation,
                            java.util.Comparator.nullsLast(java.util.Comparator.reverseOrder())))
                    .map(this::toDto).toList();
        }

        // Everyone else: always scoped to their own entreprise, ignore requestedEntrepriseId
        Integer entrepriseId = SecurityContextUtils.getEntrepriseId();
        if (entrepriseId == null) return List.of();

        return besoinFormationRepository.findByEntreprise_IdEntreprise(entrepriseId)
                .stream()
                .filter(besoin -> !"BROUILLON".equals(besoin.getStatus()))
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
            if (currentUserHasDepartmentScope() && (besoin.getDepartement() == null
                    || !requireCurrentDepartment().equals(besoin.getDepartement().getId()))) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès refusé");
            }
        }

        if (currentUserHasDepartmentScope() && !isVisibleToCurrentRequester(besoin)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        return toDto(besoin);
    }

    // =========================
    // CREATE (ADMIN only — also enforced via @PreAuthorize on the controller)
    // =========================
    public BesoinFormationResponseDto createBesoin(BesoinFormationRequest request) {
        boolean chef = currentUserIsDepartmentChef();
        boolean manager = currentUserIsManager();
        boolean departmentScopedCreator = chef || manager;
        if (!departmentScopedCreator && request.idEntreprise() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Entreprise obligatoire");
        }
        if (request.intitule() == null || request.intitule().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "L'intitulé du besoin est obligatoire");
        }

        Integer entrepriseId = departmentScopedCreator
                ? SecurityContextUtils.getEntrepriseId()
                : request.idEntreprise();
        if (manager && !java.util.Objects.equals(entrepriseId, request.idEntreprise())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Vous ne pouvez créer un besoin que pour votre entreprise.");
        }
        if (entrepriseId == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Entreprise obligatoire");
        Entreprise entreprise = entrepriseRepository.findById(entrepriseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Entreprise non trouvée"));

        Departement departement = null;
        User requester = null;
        if (departmentScopedCreator) {
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
                .status(chef ? "BROUILLON" : "APPROVED")
                .nouvelleFormation(Boolean.TRUE.equals(request.nouvelleFormation()))
                .dept(departmentScopedCreator ? departement.getNom() : request.dept())
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
        if (!currentUserIsAdminOnly()) {
            ensureManagerCanDecide(besoin);
        }
        if (!"PENDING".equalsIgnoreCase(besoin.getStatus() == null ? "" : besoin.getStatus().trim())) {
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
        if ("APPROVED".equals(status) && besoin.isNouvelleFormation()
                && decision.intitule() != null && !decision.intitule().isBlank()) {
            besoin.setIntitule(decision.intitule().trim());
        }
        besoin.setStatus(status);
        besoin.setRejectionReason("REJECTED".equals(status) ? reason : null);
        return toDto(besoinFormationRepository.save(besoin));
    }

    private void ensureManagerCanDecide(BesoinFormation besoin) {
        User requester = besoin.getRequestedBy();
        Integer managerEntrepriseId = SecurityContextUtils.getEntrepriseId();
        Integer managerDepartementId = SecurityContextUtils.getDepartementId();
        Integer requestEntrepriseId = besoin.getEntreprise() != null
                ? besoin.getEntreprise().getIdEntreprise() : null;
        Integer requestDepartementId = besoin.getDepartement() != null
                ? besoin.getDepartement().getId() : null;

        if (!currentUserIsManager()
                || requester == null
                || !"CHEF_DEPARTEMENT".equals(requester.getRole())
                || managerEntrepriseId == null
                || managerDepartementId == null
                || !managerEntrepriseId.equals(requestEntrepriseId)
                || !managerDepartementId.equals(requestDepartementId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Un manager peut traiter uniquement les demandes des chefs de son département.");
        }
    }

    public BesoinFormationResponseDto submitDraft(Integer id) {
        BesoinFormation besoin = besoinFormationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Besoin de formation non trouvé"));
        ensureRequesterOwnsDraft(besoin);
        besoin.setStatus("PENDING");
        besoin.setRejectionReason(null);
        return toDto(besoinFormationRepository.save(besoin));
    }

    @org.springframework.transaction.annotation.Transactional
    public List<BesoinFormationResponseDto> submitDrafts(List<Integer> ids) {
        if (ids == null || ids.isEmpty() || ids.stream().anyMatch(java.util.Objects::isNull)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sélectionnez au moins un brouillon.");
        }

        Set<Integer> uniqueIds = new LinkedHashSet<>(ids);
        List<BesoinFormation> drafts = besoinFormationRepository.findAllById(uniqueIds);
        if (drafts.size() != uniqueIds.size()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Un ou plusieurs besoins sont introuvables.");
        }

        // Validate every request before changing any of them, so a bulk submit is all-or-nothing.
        drafts.forEach(this::ensureRequesterOwnsDraft);
        drafts.forEach(besoin -> {
            besoin.setStatus("PENDING");
            besoin.setRejectionReason(null);
        });
        return besoinFormationRepository.saveAll(drafts).stream().map(this::toDto).toList();
    }

    // =========================
    // UPDATE
    // =========================
    public BesoinFormationResponseDto updateBesoin(Integer id, BesoinFormationRequest request) {
        BesoinFormation existing = besoinFormationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Besoin de formation non trouvé"));

        boolean admin = currentUserIsAdminOnly();
        if (admin) {
            // Admins can edit any besoin.
        } else if (currentUserIsDepartmentChef()) {
            ensureRequesterOwnsDraft(existing);
        } else if (currentUserIsManager()) {
            ensureManagerCanManageSubmittedChefRequest(existing);
        } else if (existing.getRequestedBy() != null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Seul l'administrateur peut modifier cette demande.");
        }

        boolean preserveRequesterScope = existing.getRequestedBy() != null;
        if (!preserveRequesterScope && request.idEntreprise() != null) {
            Entreprise entreprise = entrepriseRepository.findById(request.idEntreprise())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Entreprise non trouvée"));
            existing.setEntreprise(entreprise);
        }

        if (!preserveRequesterScope && request.dept() != null) existing.setDept(request.dept());
        if (request.intitule() != null)           existing.setIntitule(request.intitule());
        if (request.nouvelleFormation() != null)  existing.setNouvelleFormation(request.nouvelleFormation());
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

    private void ensureManagerCanManageSubmittedChefRequest(BesoinFormation besoin) {
        if (!"PENDING".equalsIgnoreCase(besoin.getStatus() == null ? "" : besoin.getStatus().trim())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Le manager peut modifier uniquement une demande envoyée et en attente.");
        }
        ensureManagerCanDecide(besoin);
    }

    private void ensureRequesterOwnsDraft(BesoinFormation besoin) {
        if (!currentUserCanSubmitRequests()
                || besoin.getRequestedBy() == null
                || !SecurityContextUtils.getEmail().equalsIgnoreCase(besoin.getRequestedBy().getEmail())
                || !"BROUILLON".equals(besoin.getStatus())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Seul le créateur peut modifier son brouillon avant son envoi à l'administrateur.");
        }
    }

    private boolean isVisibleToCurrentRequester(BesoinFormation besoin) {
        User requester = besoin.getRequestedBy();
        if (requester == null) return false;

        if (currentUserIsManager()) {
            if ("MANAGER".equals(requester.getRole())) {
                return !"BROUILLON".equals(besoin.getStatus());
            }
            return "CHEF_DEPARTEMENT".equals(requester.getRole())
                    && !"BROUILLON".equals(besoin.getStatus());
        }

        String currentEmail = SecurityContextUtils.getEmail();
        if (currentEmail != null && currentEmail.equalsIgnoreCase(requester.getEmail())) return true;
        return false;
    }

    // =========================
    // DELETE (ADMIN only)
    // =========================
    public void deleteBesoin(Integer id) {
        BesoinFormation besoin = besoinFormationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Besoin de formation non trouvé"));

        if (currentUserIsManager()) {
            ensureManagerCanManageSubmittedChefRequest(besoin);
        } else if (!currentUserIsAdminOnly() && !currentUserIsAdminFinance()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès refusé.");
        }

        besoinFormationRepository.delete(besoin);
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
                b.getRejectionReason(),
                b.getRequestedBy() != null ? b.getRequestedBy().getRole() : null,
                b.getRequestedBy() != null ? b.getRequestedBy().getId() : null,
                b.isNouvelleFormation()
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

    private boolean currentUserIsManager() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(authority -> "MANAGER".equals(authority.getAuthority()));
    }

    private boolean currentUserIsAdminFinance() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(authority -> "ADMIN_FINANCE".equals(authority.getAuthority()));
    }

    private boolean currentUserCanSubmitRequests() {
        return currentUserIsDepartmentChef();
    }

    private boolean currentUserHasDepartmentScope() {
        return currentUserIsDepartmentChef() || currentUserIsManager();
    }

    private Integer requireCurrentDepartment() {
        Integer id = SecurityContextUtils.getDepartementId();
        if (id == null) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Aucun département n'est associé à ce compte.");
        return id;
    }
}
