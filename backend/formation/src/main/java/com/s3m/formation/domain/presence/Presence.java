package com.s3m.formation.domain.presence;

import com.s3m.formation.domain.participation.Participation;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(
        name = "presence",
        uniqueConstraints = @UniqueConstraint(columnNames = {"participation_id", "jour"})
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Presence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participation_id", nullable = false)
    private Participation participation;

    @Column(name = "jour", nullable = false)
    private LocalDate jour;

    @Column(name = "present", nullable = false)
    private Boolean present = false;
}