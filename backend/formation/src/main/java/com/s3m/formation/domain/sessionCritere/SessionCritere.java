package com.s3m.formation.domain.sessionCritere;

import com.s3m.formation.domain.sessionFormation.SessionFormation;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "session_critere",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"id_session", "jour", "critere_index"}
        )
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SessionCritere {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_session", nullable = false)
    private SessionFormation session;

    @Column(nullable = false)
    private Integer jour;

    @Column(name = "critere_index", nullable = false)
    private Integer critereIndex;

    @Column(nullable = false, length = 500)
    private String libelle;
}