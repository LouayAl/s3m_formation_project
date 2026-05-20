package com.s3m.formation.domain.employe;

import com.s3m.formation.api.dto.EmployeResponseDto;
import com.s3m.formation.domain.departement.Departement;
import com.s3m.formation.domain.departement.DepartementRepository;
import com.s3m.formation.domain.entreprise.Entreprise;
import com.s3m.formation.domain.entreprise.EntrepriseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EmployeService {

    private final EmployeRepository employeRepository;
    private final EntrepriseRepository entrepriseRepository;
    private final DepartementRepository departementRepository;

    // =========================
    // GET ALL (kept for internal use e.g. ParticipantsModal)
    // =========================
    public List<EmployeResponseDto> getAllEmployes() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer entrepriseId = (Integer) auth.getDetails();

        return employeRepository.findByEntreprise_IdEntreprise(entrepriseId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    // =========================
    // GET PAGINATED
    // =========================
    public Page<EmployeResponseDto> getEmployesPaginated(
            Integer entrepriseId, String search,
            int page, int size,
            String sortBy, String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return employeRepository
                .findPaginated(entrepriseId, search, pageable)
                .map(this::toDto);
    }

    // =========================
    // GET BY ID
    // =========================
    public EmployeResponseDto getEmployeById(Integer id) {
        Employe employe = employeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Employé non trouvé"));
        return toDto(employe);
    }

    public List<EmployeResponseDto> searchEmployes(String keyword) {
        return employeRepository.search(keyword)
                .stream()
                .map(this::toDto)
                .toList();
    }

    // =========================
    // CREATE
    // =========================
    public EmployeResponseDto createEmploye(Employe employe) {

        if (employe.getEntreprise() == null || employe.getEntreprise().getIdEntreprise() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Entreprise obligatoire");
        }

        Entreprise entreprise = entrepriseRepository.findById(employe.getEntreprise().getIdEntreprise())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Entreprise non trouvée"));

        Departement departement = null;
        if (employe.getDepartement() != null && employe.getDepartement().getId() != null) {
            departement = departementRepository.findById(employe.getDepartement().getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Département non trouvé"));
        }

        employe.setEntreprise(entreprise);
        employe.setDepartement(departement);
        employe.setMatricule(blankToNull(employe.getMatricule()));

        try {
            Employe saved = employeRepository.saveAndFlush(employe);
            return toDto(saved);
        } catch (Exception e) {
            log.error("Error while saving employee", e);
            throw e;
        }
    }

    // =========================
    // UPDATE
    // =========================
    public EmployeResponseDto updateEmploye(Integer id, Employe updated) {

        Employe existing = employeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employé non trouvé"));

        existing.setNom(updated.getNom());
        existing.setPrenom(updated.getPrenom());
        existing.setEmail(updated.getEmail());
        existing.setTelephone(updated.getTelephone());
        existing.setCin(updated.getCin());
        existing.setCnss(updated.getCnss());
        existing.setMatricule(blankToNull(updated.getMatricule()));
        existing.setCsp(updated.getCsp());
        existing.setFonction(updated.getFonction());
        existing.setTypeContrat(updated.getTypeContrat());
        existing.setF_h(updated.getF_h());
        existing.setDateEmbauche(updated.getDateEmbauche());
        existing.setDateNaissance(updated.getDateNaissance());

        if (updated.getEntreprise() != null && updated.getEntreprise().getIdEntreprise() != null) {
            Entreprise entreprise = entrepriseRepository
                    .findById(updated.getEntreprise().getIdEntreprise())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Entreprise non trouvée"));
            existing.setEntreprise(entreprise);
        }

        if (updated.getDepartement() != null && updated.getDepartement().getId() != null) {
            Departement departement = departementRepository
                    .findById(updated.getDepartement().getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Département non trouvé"));
            existing.setDepartement(departement);
        } else {
            existing.setDepartement(null);
        }

        try {
            Employe saved = employeRepository.saveAndFlush(existing);
            return toDto(saved);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Matricule ou email déjà existant");
        }
    }

    // =========================
    // DELETE
    // =========================
    public void deleteEmploye(Integer id) {
        if (!employeRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Employé non trouvé");
        }
        employeRepository.deleteById(id);
    }

    // =========================
    // IMPORT EXCEL
    // =========================
    public int importFromExcel(MultipartFile file) {

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            if (headerRow == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Le fichier Excel ne contient pas d'en-tête."
                );
            }

            Map<String, Integer> headers = getHeaderMap(headerRow);

            List<Employe> toSave = new ArrayList<>();

            int imported = 0;
            int skipped  = 0;

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);

                if (row == null) continue;

                String nom    = blankToNull(getString(row, headers, "nom"));
                String prenom = blankToNull(getString(row, headers, "prenom"));

                // Skip empty rows
                if (nom == null || prenom == null) {
                    continue;
                }

                String cin        = blankToNull(getString(row, headers, "cin"));
                String matricule  = blankToNull(getString(row, headers, "matricule"));
                String email      = blankToNull(getString(row, headers, "email"));

                // Duplicate checks
                if (cin != null && employeRepository.existsByCin(cin)) {
                    skipped++;
                    continue;
                }

                if (matricule != null && employeRepository.existsByMatricule(matricule)) {
                    skipped++;
                    continue;
                }

                if (email != null && employeRepository.existsByEmail(email)) {
                    skipped++;
                    continue;
                }

                String entrepriseNom  = blankToNull(getString(row, headers, "entreprise"));
                String departementNom = blankToNull(getString(row, headers, "departement"));

                if (entrepriseNom == null) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Entreprise manquante à la ligne " + (i + 1)
                    );
                }

                if (departementNom == null) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Département manquant à la ligne " + (i + 1)
                    );
                }

                Entreprise entreprise = entrepriseRepository
                        .findByNomEntrepriseIgnoreCase(entrepriseNom)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Entreprise inconnue : " + entrepriseNom
                                )
                        );

                Departement departement = departementRepository
                        .findByNomIgnoreCase(departementNom)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Département inconnu : " + departementNom
                                )
                        );

                Employe emp = new Employe();

                emp.setNom(nom);
                emp.setPrenom(prenom);

                emp.setCin(cin);
                emp.setCnss(blankToNull(getString(row, headers, "cnss")));
                emp.setMatricule(matricule);

                emp.setCsp(blankToNull(getString(row, headers, "csp")));
                emp.setFonction(blankToNull(getString(row, headers, "fonction")));
                emp.setTypeContrat(blankToNull(getString(row, headers, "type_contrat")));

                emp.setTelephone(blankToNull(getString(row, headers, "telephone")));
                emp.setEmail(email);

                String gender = blankToNull(getString(row, headers, "f_h"));

                if (gender != null && !gender.isBlank()) {
                    emp.setF_h(gender.trim().toUpperCase().charAt(0));
                }

                emp.setDateEmbauche(getDate(row, headers, "date_embauche"));
                emp.setDateNaissance(getDate(row, headers, "date_naissance"));

                emp.setEntreprise(entreprise);
                emp.setDepartement(departement);

                toSave.add(emp);

                imported++;

                System.out.println(
                        "IMPORTÉ => "
                                + nom + " "
                                + prenom
                                + " | CIN=" + cin
                                + " | MATRICULE=" + matricule
                );
            }

            employeRepository.saveAll(toSave);

            System.out.println("Import terminé : "
                    + imported + " importés, "
                    + skipped + " ignorés.");

            return imported;

        } catch (ResponseStatusException ex) {

            throw ex;

        } catch (Exception e) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Erreur import Excel Employés : " + e.getMessage()
            );
        }
    }
    // =========================
    // HELPERS
    // =========================
    private Map<String, Integer> getHeaderMap(Row headerRow) {
        Map<String, Integer> map = new HashMap<>();
        for (Cell cell : headerRow) {
            map.put(cell.getStringCellValue().trim().toLowerCase(), cell.getColumnIndex());
        }
        return map;
    }

    private String getCellValue(Cell cell) {

        if (cell == null) return "";

        return switch (cell.getCellType()) {

            case STRING ->
                    cell.getStringCellValue().trim();

            case NUMERIC ->
                    String.valueOf((long) cell.getNumericCellValue());

            case BOOLEAN ->
                    String.valueOf(cell.getBooleanCellValue());

            default -> "";
        };
    }

    private String getString(Row row, Map<String, Integer> headers, String key) {

        Integer index = headers.get(key);

        if (index == null) return "";

        Cell cell = row.getCell(index);

        return getCellValue(cell);
    }

    private String blankToNull(String value) {
        if (value == null || value.isBlank()) return null;
        return value.trim();
    }

    private LocalDate getDate(Row row, Map<String, Integer> headers, String col) {
        Integer index = headers.get(col.toLowerCase());
        if (index == null) return null;
        Cell cell = row.getCell(index);
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getLocalDateTimeCellValue().toLocalDate();
        }
        return null;
    }

    // =========================
    // DTO MAPPER
    // =========================
    private EmployeResponseDto toDto(Employe employe) {
        return new EmployeResponseDto(
                employe.getIdEmploye(),
                employe.getNom(),
                employe.getPrenom(),
                employe.getEmail(),
                employe.getTelephone(),
                employe.getCin(),
                employe.getCnss(),
                employe.getMatricule(),
                employe.getCsp(),
                employe.getFonction(),
                employe.getTypeContrat(),
                employe.getF_h(),
                employe.getDateEmbauche(),
                employe.getDateNaissance(),
                employe.getEntreprise() != null ? employe.getEntreprise().getIdEntreprise() : null,
                employe.getEntreprise() != null ? employe.getEntreprise().getNomEntreprise() : null,
                employe.getDepartement() != null ? employe.getDepartement().getId() : null,
                employe.getDepartement() != null ? employe.getDepartement().getNom() : null
        );
    }
}
