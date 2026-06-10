package com.s3m.formation.domain.presence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PresenceRepository extends JpaRepository<Presence, Integer> {

    // All presence records for a session on a given day
    @Query("""
        SELECT p FROM Presence p
        JOIN FETCH p.participation part
        JOIN FETCH part.employe e
        WHERE part.session.idSession = :sessionId
          AND p.jour = :jour
        ORDER BY e.nom, e.prenom
    """)
    List<Presence> findBySessionAndJour(
            @Param("sessionId") Integer sessionId,
            @Param("jour") LocalDate jour
    );

    // All distinct days that have presence records for a session
    @Query("""
        SELECT DISTINCT p.jour FROM Presence p
        WHERE p.participation.session.idSession = :sessionId
        ORDER BY p.jour
    """)
    List<LocalDate> findJoursBySession(@Param("sessionId") Integer sessionId);

    // Find one record to upsert
    Optional<Presence> findByParticipation_IdParticipationAndJour(
            Integer participationId, LocalDate jour);

    // All presence for a session (used for export)
    @Query("""
        SELECT p FROM Presence p
        JOIN FETCH p.participation part
        JOIN FETCH part.employe e
        WHERE part.session.idSession = :sessionId
        ORDER BY p.jour, e.nom
    """)
    List<Presence> findAllBySession(@Param("sessionId") Integer sessionId);
}