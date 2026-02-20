package com.s3m.formation.domain.employe;

import com.s3m.formation.api.dto.EmployeResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    // GET ALL
    // =========================
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public List<EmployeResponseDto> getAllEmployes() {
        return employeService.getAllEmployes();
    }

    // =========================
    // GET BY ID
    // =========================
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
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
    @PreAuthorize("hasAuthority('ADMIN')")
    public EmployeResponseDto create(@RequestBody Employe employe) {

        EmployeResponseDto created = employeService.createEmploye(employe);

        return created;
    }

    // =========================
    // UPDATE
    // =========================
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public EmployeResponseDto updateEmploye(@PathVariable Integer id, @RequestBody Employe employe) {

        EmployeResponseDto updated = employeService.updateEmploye(id, employe);

        return updated;
    }

    // =========================
    // DELETE
    // =========================
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ADMIN')")
    public void delete(@PathVariable Integer id) {
        employeService.deleteEmploye(id);
    }

    @PostMapping("/import")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String importEmployesExcel(@RequestParam("file") MultipartFile file) {

        int imported = employeService.importFromExcel(file);

        return imported + " employés importés avec succès.";
    }

}
