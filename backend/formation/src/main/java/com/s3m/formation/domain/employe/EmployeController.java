package com.s3m.formation.domain.employe;

import com.s3m.formation.api.dto.EmployeResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/employes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EmployeController {

    private final EmployeService employeService;

    // =========================
    // GET ALL (kept for ParticipantsModal and other internal uses)
    // =========================
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','EQUIPMENT_MANAGER','TRAINER')")
    public List<EmployeResponseDto> getAllEmployes() {
        return employeService.getAllEmployes();
    }

    // =========================
    // GET PAGINATED
    // Usage: GET /api/employes/paginated?page=0&size=20&search=john&entrepriseId=4
    // =========================
    @GetMapping("/paginated")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','EQUIPMENT_MANAGER','TRAINER')")
    public Page<EmployeResponseDto> getEmployesPaginated(
            @RequestParam(defaultValue = "0")          int page,
            @RequestParam(defaultValue = "20")         int size,
            @RequestParam(required = false)            String search,
            @RequestParam(required = false)            Integer entrepriseId,
            @RequestParam(defaultValue = "idEmploye")  String sortBy,
            @RequestParam(defaultValue = "desc")       String sortDir
    ) {
        return employeService.getEmployesPaginated(entrepriseId, search, page, size, sortBy, sortDir);
    }

    // =========================
    // GET BY ID
    // =========================
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','EQUIPMENT_MANAGER','TRAINER')")
    public EmployeResponseDto getEmployeById(@PathVariable Integer id) {
        return employeService.getEmployeById(id);
    }

    // =========================
    // SEARCH
    // =========================
    @GetMapping("/search")
    public List<EmployeResponseDto> search(@RequestParam String keyword) {
        return employeService.searchEmployes(keyword);
    }

    // =========================
    // CREATE
    // =========================
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ADMIN','EQUIPMENT_MANAGER','TRAINER')")
    public EmployeResponseDto create(@RequestBody Employe employe) {
        return employeService.createEmploye(employe);
    }

    // =========================
    // UPDATE
    // =========================
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN','EQUIPMENT_MANAGER','TRAINER')")
    public EmployeResponseDto updateEmploye(@PathVariable Integer id, @RequestBody Employe employe) {
        return employeService.updateEmploye(id, employe);
    }

    // =========================
    // DELETE
    // =========================
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ADMIN','EQUIPMENT_MANAGER','TRAINER')")
    public void delete(@PathVariable Integer id) {
        employeService.deleteEmploye(id);
    }

    // =========================
    // IMPORT EXCEL
    // =========================
    @PostMapping("/import")
    @PreAuthorize("hasAuthority('ADMIN','EQUIPMENT_MANAGER')")
    public String importEmployesExcel(@RequestParam("file") MultipartFile file) {
        int imported = employeService.importFromExcel(file);
        return imported + " employés importés avec succès.";
    }
}