package com.s3m.formation.domain.besoinFormation;

import com.s3m.formation.api.dto.BesoinDecisionRequest;
import com.s3m.formation.api.dto.BesoinFormationRequest;
import com.s3m.formation.auth.repository.UserRepository;
import com.s3m.formation.domain.departement.DepartementRepository;
import com.s3m.formation.domain.entreprise.Entreprise;
import com.s3m.formation.domain.entreprise.EntrepriseRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BesoinFormationServiceTest {
    private final BesoinFormationRepository repository = mock(BesoinFormationRepository.class);
    private final EntrepriseRepository entreprises = mock(EntrepriseRepository.class);
    private final BesoinFormationService service = new BesoinFormationService(repository, entreprises,
            mock(DepartementRepository.class), mock(UserRepository.class));

    @AfterEach
    void clearAuthentication() {
        SecurityContextHolder.clearContext();
    }

    @ParameterizedTest
    @ValueSource(strings = {"ADMIN", "MANAGER"})
    void managementPrivilegesDoNotRequireAnAssignedCompanyOrDepartment(String role) {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                "reviewer@example.com", null, List.of(new SimpleGrantedAuthority(role))));
        Entreprise entreprise = new Entreprise();
        entreprise.setIdEntreprise(42);
        BesoinFormation besoin = BesoinFormation.builder().idBesoin(1).entreprise(entreprise)
                .intitule("Formation").status("PENDING").build();
        when(repository.findAll()).thenReturn(List.of(besoin));
        when(repository.findByEntreprise_IdEntreprise(42)).thenReturn(List.of(besoin));
        when(repository.findById(1)).thenReturn(Optional.of(besoin));
        when(entreprises.findById(42)).thenReturn(Optional.of(entreprise));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        assertEquals(1, service.getVisibleBesoinsForCurrentUser(null).size());
        assertEquals(1, service.getVisibleBesoinsForCurrentUser(42).size());
        assertEquals(1, service.getBesoinById(1).id());
        BesoinFormationRequest request = new BesoinFormationRequest(42, "RH", "Updated", false,
                null, null, null, null, null, null, null, null, null, null, null, null);
        assertEquals("APPROVED", service.createBesoin(request).status());
        assertEquals("Updated", service.updateBesoin(1, request).intitule());
        assertEquals("APPROVED", service.decideBesoin(1,
                new BesoinDecisionRequest("APPROVED", null, null)).status());
        besoin.setStatus("PENDING");
        assertEquals("REJECTED", service.decideBesoin(1,
                new BesoinDecisionRequest("REJECTED", "Reason", null)).status());
        service.deleteBesoin(1);
        verify(repository).delete(besoin);
    }
}
