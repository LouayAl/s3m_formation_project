package com.s3m.formation.domain.sessionProgramme;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SessionDailyProgramRepository extends JpaRepository<SessionDailyProgram, Integer> {

    Optional<SessionDailyProgram> findBySession_IdSessionAndJour(Integer idSession, Integer jour);
}
