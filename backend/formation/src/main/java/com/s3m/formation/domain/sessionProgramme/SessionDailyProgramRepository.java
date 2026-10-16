package com.s3m.formation.domain.sessionProgramme;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SessionDailyProgramRepository extends JpaRepository<SessionDailyProgram, Integer> {

    void deleteBySession_IdSession(Integer sessionId);

    Optional<SessionDailyProgram> findBySession_IdSessionAndJourAndEmployeIsNull(
            Integer idSession, Integer jour);
}
