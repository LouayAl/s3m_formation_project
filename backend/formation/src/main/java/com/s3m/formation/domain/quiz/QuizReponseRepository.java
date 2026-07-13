package com.s3m.formation.domain.quiz;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuizReponseRepository extends JpaRepository<QuizReponse, Integer> {
    boolean existsBySession_IdSessionAndEmploye_IdEmploye(Integer sessionId, Integer employeId);
    List<QuizReponse> findBySession_IdSession(Integer sessionId);

    @Query("SELECT q FROM QuizReponse q WHERE q.session.idSession = :sessionId")
    List<QuizReponse> findBySessionId(@Param("sessionId") Integer sessionId);
    @Query("SELECT q FROM QuizReponse q WHERE q.session.idSession IN :sessionIds")
    List<QuizReponse> findBySessionIdIn(@Param("sessionIds") List<Integer> sessionIds);
}