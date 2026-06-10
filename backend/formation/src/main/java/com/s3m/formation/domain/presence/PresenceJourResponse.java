package com.s3m.formation.domain.presence;

import java.time.LocalDate;
import java.util.List;

public record PresenceJourResponse(
        Integer         sessionId,
        LocalDate jour,
        List<PresenceJourDto> participants
) {}
