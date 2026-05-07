package com.s3m.formation.domain.sessionCritere;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessionCritereRepository extends JpaRepository<SessionCritere, Integer> {

    List<SessionCritere> findBySession_IdSessionAndJourOrderByCritereIndexAsc(
            Integer idSession, Integer jour
    );

    void deleteBySession_IdSessionAndJour(Integer idSession, Integer jour);
}