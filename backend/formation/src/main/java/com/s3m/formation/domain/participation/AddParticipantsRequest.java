package com.s3m.formation.domain.participation;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddParticipantsRequest {
    private List<Integer> employeIds;
}
