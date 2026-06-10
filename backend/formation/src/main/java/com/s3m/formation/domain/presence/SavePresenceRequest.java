package com.s3m.formation.domain.presence;

import java.time.LocalDate;
import java.util.List;

public record SavePresenceRequest(
        LocalDate jour,
        List<PresenceEntry> presences
) {
    public record PresenceEntry(
            Integer participationId,
            Boolean present
    ) {}
}
