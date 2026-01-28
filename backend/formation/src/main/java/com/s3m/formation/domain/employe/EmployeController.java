package com.s3m.formation.domain.employe;

import com.s3m.formation.api.dto.EmployeResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employes")
@RequiredArgsConstructor
public class EmployeController {

    private final EmployeService employeService;

    // ✅ Get all employes
    @GetMapping
    public List<EmployeResponseDto> getAllEmployes() {
        return employeService.getAllEmployes();
    }

    // ✅ Get employe by ID
    @GetMapping("/{id}")
    public EmployeResponseDto getEmployeById(@PathVariable Integer id) {
        return employeService.getEmployeById(id);
    }

    // ✅ Search employes by keyword
    @GetMapping("/search")
    public List<EmployeResponseDto> search(@RequestParam String keyword) {
        return employeService.searchEmployes(keyword);
    }

    // ✅ Create new employe
    @PostMapping
    public EmployeResponseDto create(@RequestBody Employe employe) {
        return employeService.createEmploye(employe);
    }

    // ✅ Update employe
    @PutMapping("/{id}")
    public EmployeResponseDto update(@PathVariable Integer id, @RequestBody Employe employe) {
        return employeService.updateEmploye(id, employe);
    }

    // ✅ Delete employe
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        employeService.deleteEmploye(id);
    }
}
