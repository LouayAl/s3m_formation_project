package com.s3m.formation.domain.employe;

import com.s3m.formation.api.dto.EmployeResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeService {

    private final EmployeRepository employeRepository;

    public List<EmployeResponseDto> getAllEmployes() {
        return employeRepository.findAll()
                .stream()
                .map(e -> new EmployeResponseDto(
                        e.getIdEmploye(),
                        e.getNom(),
                        e.getPrenom(),
                        e.getEmail(),
                        e.getTelephone()
                ))
                .toList();
    }
}
