// api/dto/SatisfactionKpiDto.java
package com.s3m.formation.api.dto;

public record SatisfactionKpiDto(
        double satisfactionGlobaleS3M,      // all clients, all sessions
        double satisfactionClientGlobale,   // this client, all their sessions
        double satisfactionParFormation,    // this client + this formation (all sessions)
        double satisfactionSession,         // this specific session
        int totalReponsesS3M,
        int totalReponsesClient,
        int totalReponsesFormation,
        int totalReponsesSession
) {}