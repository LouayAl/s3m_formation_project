package com.s3m.formation.domain.participation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipationRepository
        extends JpaRepository<Participation, Integer> {

    List<Participation> findBySession_IdSession(Integer sessionId);

    long countBySession_IdSession(Integer sessionId);

    boolean existsBySession_IdSessionAndEmploye_IdEmploye(Integer sessionId, Integer empId);
}
