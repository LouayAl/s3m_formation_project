package com.s3m.formation.domain.entreprise;

import com.s3m.formation.api.dto.EntrepriseResponseDto;
import com.s3m.formation.domain.employe.EmployeRepository;
import com.s3m.formation.domain.sessionFormation.SessionFormationRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class EntrepriseService {
    @Autowired
    private final EntrepriseRepository entrepriseRepository;

    @Autowired
    private final EmployeRepository employeRepository;

    @Autowired
    private final SessionFormationRepository sessionFormationRepository;

    // Get all entreprises
    public List<EntrepriseResponseDto> getAllEntreprises() {
        return entrepriseRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    // Get entreprise by ID
    public EntrepriseResponseDto getEntrepriseById(Integer id) {
        Entreprise e = entrepriseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entreprise not found"));
        return toDto(e);
    }

    public List<EntrepriseResponseDto> getEntreprises(TypeEntreprise type) {
        List<Entreprise> entreprises = (type != null)
                ? entrepriseRepository.findByTypeEntreprise(type)
                : entrepriseRepository.findAll();
        return entreprises.stream().map(this::toDto).toList();
    }

    // Search entreprises by name
    public List<EntrepriseResponseDto> searchEntreprises(String keyword) {
        return entrepriseRepository.search(keyword)
                .stream()
                .map(this::toDto)
                .toList();
    }

    // Create entreprise
    public EntrepriseResponseDto createEntreprise(Entreprise entreprise) {
        if (entreprise.getTypeEntreprise() == null) {
            entreprise.setTypeEntreprise(TypeEntreprise.AUTRE);
        }
        Entreprise saved = entrepriseRepository.save(entreprise);
        return toDto(saved);
    }

    // Update entreprise
    public EntrepriseResponseDto updateEntreprise(Integer id, Entreprise updated) {
        Entreprise existing = entrepriseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entreprise not found"));

        existing.setNomEntreprise(updated.getNomEntreprise());
        Entreprise saved = entrepriseRepository.save(existing);
        return toDto(saved);
    }

    // Delete entreprise
    public void deleteEntreprise(Integer id) {
        // Check if entreprise exists first
        Entreprise entreprise = entrepriseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Entreprise non trouvée"));
        // Check if there are employees linked
        boolean hasEmployees = employeRepository.existsByEntreprise_IdEntreprise(id);
        if (hasEmployees) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Impossible de supprimer cette entreprise : elle possède des employés assignés."
            );
        }
        // Optional: check if there are sessions linked
        boolean hasSessions = sessionFormationRepository.existsByEntreprise_IdEntreprise(id);
        if (hasSessions) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Impossible de supprimer cette entreprise : elle possède des sessions de formation assignées."
            );
        }
        entrepriseRepository.deleteById(id);
    }

    public void importFromExcel(MultipartFile file) {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) return;

            List<Entreprise> entreprisesToSave = new ArrayList<>();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // skip header
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Cell cell = row.getCell(0); // first column: nomEntreprise
                if (cell == null) continue;

                String nomEntreprise = cell.getStringCellValue().trim();
                if (nomEntreprise.isEmpty()) continue;

                // ✅ Check for duplicates in DB before adding
                if (entrepriseRepository.existsByNomEntreprise(nomEntreprise)) {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "Entreprise déjà existante : " + nomEntreprise
                    );
                }

                Entreprise entreprise = new Entreprise();
                entreprise.setNomEntreprise(nomEntreprise);
                entreprise.setTypeEntreprise(TypeEntreprise.AUTRE);
                entreprisesToSave.add(entreprise);
            }

            // Save all non-duplicate entreprises
            entrepriseRepository.saveAll(entreprisesToSave);

        } catch (ResponseStatusException ex) {
            // Re-throw so GlobalExceptionHandler handles it
            throw ex;
        } catch (Exception e) {
            throw new RuntimeException("Erreur import Excel Entreprises", e);
        }
    }



    // Convert entity to DTO
    private EntrepriseResponseDto toDto(Entreprise entreprise) {
        return new EntrepriseResponseDto(
                entreprise.getIdEntreprise(),
                entreprise.getNomEntreprise(),
                entreprise.getTypeEntreprise()
        );
    }


}
