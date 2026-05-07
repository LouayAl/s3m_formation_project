package com.s3m.formation.api.dto;

import java.util.List;

public record SessionCritereRequest(
        List<String> libelles  // ordered list of criteria labels for that day
) {}