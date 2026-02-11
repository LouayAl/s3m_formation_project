package com.s3m.formation.domain.employe;

import com.s3m.formation.api.dto.EmployeResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
        log.info("GET /api/employes");
        return employeService.getAllEmployes();
    }

    // =========================
    // GET BY ID
    // =========================
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public EmployeResponseDto getEmployeById(@PathVariable Integer id) {
        log.info("GET /api/employes/{}", id);
        return employeService.getEmployeById(id);
    }

    // =========================
    // SEARCH
    // =========================
    @GetMapping("/search")
    public List<EmployeResponseDto> search(@RequestParam String keyword) {
        log.info("GET /api/employes/search?keyword={}", keyword);
        return employeService.searchEmployes(keyword);
    }

    // =========================
    // CREATE
    // =========================
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ADMIN')")
    public EmployeResponseDto create(@RequestBody Employe employe) {
        log.info("POST /api/employes");
        log.info("Payload received: {}", employe);

        EmployeResponseDto created = employeService.createEmploye(employe);

        log.info("Employee successfully created with id={}", created.getIdEmploye());
        return created;
    }

    // =========================
    // UPDATE
    // =========================
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public EmployeResponseDto updateEmploye(@PathVariable Integer id, @RequestBody Employe employe) {
        log.info("PUT /api/employes/{}", id);
        log.info("Payload received: {}", employe);

        EmployeResponseDto updated = employeService.updateEmploye(id, employe);

        log.info("Employee successfully updated with id={}", id);
        return updated;
    }

    // =========================
    // DELETE
    // =========================
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ADMIN')")
    public void delete(@PathVariable Integer id) {
        log.info("DELETE /api/employes/{}", id);
        employeService.deleteEmploye(id);
        log.info("Employee deleted with id={}", id);
    }
}
