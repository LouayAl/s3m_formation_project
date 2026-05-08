package com.s3m.formation.domain.formation;

import com.s3m.formation.api.dto.FormationResponseDto;
import com.s3m.formation.domain.sessionFormation.SessionFormationRepository;
import com.s3m.formation.security.util.SecurityContextUtils;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class FormationService {

    private final FormationRepository formationRepository;
    private final SessionFormationRepository sessionFormationRepository;

    /* =========================
       READ
       ========================= */

    public List<FormationResponseDto> getAllFormations() {
        return formationRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<FormationResponseDto> getVisibleFormationsForCurrentUser() {
        if (!mustScopeFormationsToEntreprise()) {
            return getAllFormations();
        }

        Integer entrepriseId = SecurityContextUtils.getEntrepriseId();
        if (entrepriseId == null) {
            return List.of();
        }

        return sessionFormationRepository.findDistinctFormationsByEntrepriseId(entrepriseId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public FormationResponseDto getFormationById(Integer id) {
        Formation formation = formationRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Formation introuvable"));
        return toDto(formation);
    }

    public FormationResponseDto getFormationByReference(String reference) {
        Formation formation = formationRepository.findByReferenceFormation(reference)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Formation introuvable"));
        return toDto(formation);
    }

    public List<FormationResponseDto> searchFormations(String keyword) {
        return formationRepository.search(keyword)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<FormationResponseDto> filterFormations(
            String module,
            String famille,
            String type,
            String sousFamille
    ) {
        return formationRepository.search(module, type, famille, sousFamille)
                .stream()
                .map(this::toDto)
                .toList();
    }

    /* =========================
       CRUD
       ========================= */

    public FormationResponseDto createFormation(Formation formation) {
        return toDto(formationRepository.save(formation));
    }

    public FormationResponseDto updateFormation(Integer id, Formation updated) {

        Formation existing = formationRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Formation introuvable"));

        existing.setModule(updated.getModule());
        existing.setTypeFormation(updated.getTypeFormation());
        existing.setFamilleFormation(updated.getFamilleFormation());
        existing.setSousFamille(updated.getSousFamille());
        existing.setInterneExterne(updated.getInterneExterne());
        existing.setReferenceFormation(updated.getReferenceFormation());
        existing.setAnnee(updated.getAnnee());

        existing.setPrixHeureMad(updated.getPrixHeureMad());
        existing.setPrixJourMad(updated.getPrixJourMad());
        existing.setDureeHeures(updated.getDureeHeures());
        existing.setDureeJours(updated.getDureeJours());

        return toDto(formationRepository.save(existing));
    }

    public void deleteFormation(Integer id) {
        formationRepository.deleteById(id);
    }

    /* =========================
       IMPORT EXCEL
       ========================= */

    public String importFromExcel(MultipartFile file) {

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(0);

            // Header row
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Le fichier Excel ne contient pas d'en-têtes."
                );
            }

            Map<String, Integer> headers = buildHeaderMap(headerRow);

            List<Formation> toSave = new ArrayList<>();

            int imported = 0;
            int skipped = 0;

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);
                if (row == null) continue;

                String module = getString(row, headers, "module");
                String reference = getString(row, headers, "reference_formation");

                if (reference == null || reference.isBlank()) {
                    skipped++;
                    continue;
                }

                // Skip duplicates
                if (formationRepository.existsByReferenceFormation(reference)) {
                    skipped++;
                    continue;
                }

                Formation f = new Formation();

                f.setModule(module);
                f.setFamilleFormation(getString(row, headers, "famille_formation"));
                f.setTypeFormation(getString(row, headers, "type_formation"));
                f.setSousFamille(getString(row, headers, "sous_famille"));
                f.setInterneExterne(getString(row, headers, "interne_externe"));

                f.setReferenceFormation(reference);

                // ✅ Map ANNEE correctly
                f.setAnnee(getInteger(row, headers, "annee"));

                f.setDureeHeures(getDecimal(row, headers, "d_heures"));
                f.setDureeJours(getDecimal(row, headers, "d_jours"));

                f.setPrixHeureMad(getDecimal(row, headers, "prix_heure_mad"));
                f.setPrixJourMad(getDecimal(row, headers, "prix_jour_mad"));

                toSave.add(f);
                imported++;
            }

            formationRepository.saveAll(toSave);

            // ✅ Return message for snackbar
            return "Import terminé : " + imported +
                    " formations ajoutées, " + skipped + " ignorées.";

        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Certaines formations existent déjà dans la base."
            );
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Erreur import Excel : " + e.getMessage()
            );
        }
    }

    /* =========================
       HELPERS
       ========================= */

    private Map<String, Integer> buildHeaderMap(Row headerRow) {

        Map<String, Integer> map = new HashMap<>();

        for (Cell cell : headerRow) {
            String name = cell.getStringCellValue()
                    .trim()
                    .toLowerCase();
            map.put(name, cell.getColumnIndex());
        }

        return map;
    }

    private String getString(Row row, Map<String, Integer> headers, String col) {

        Integer index = headers.get(col.toLowerCase());
        if (index == null) return null;

        Cell cell = row.getCell(index);
        if (cell == null) return null;

        return cell.toString().trim();
    }

    private Integer getInteger(Row row, Map<String, Integer> headers, String col) {

        Integer index = headers.get(col.toLowerCase());
        if (index == null) return null;

        Cell cell = row.getCell(index);
        if (cell == null) return null;

        try {
            return (int) cell.getNumericCellValue();
        } catch (Exception e) {
            return null;
        }
    }

    private BigDecimal getDecimal(Row row, Map<String, Integer> headers, String col) {

        Integer index = headers.get(col.toLowerCase());
        if (index == null) return null;

        Cell cell = row.getCell(index);
        if (cell == null) return null;

        try {
            return BigDecimal.valueOf(cell.getNumericCellValue());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Prevent numeric overflow (precision 5, scale 2)
     * Max allowed ≈ 999.99
     */
    private BigDecimal limitDecimal(BigDecimal value) {
        if (value == null) return null;
        if (value.compareTo(BigDecimal.valueOf(999.99)) > 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Valeur trop grande : " + value + " (max = 999.99)"
            );
        }
        return value;
    }

    private boolean mustScopeFormationsToEntreprise() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;

        return auth.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .anyMatch(role -> "EQUIPMENT_MANAGER".equals(role) || "TRAINER".equals(role));
    }

    /* =========================
       DTO MAPPING
       ========================= */

    private FormationResponseDto toDto(Formation formation) {
        return new FormationResponseDto(
                formation.getIdFormation(),
                formation.getModule(),
                formation.getTypeFormation(),
                formation.getFamilleFormation(),
                formation.getInterneExterne(),
                formation.getSousFamille(),
                formation.getReferenceFormation(),
                formation.getAnnee(),
                formation.getDureeHeures(),
                formation.getDureeJours(),
                formation.getPrixHeureMad(),
                formation.getPrixJourMad()
        );
    }
}
