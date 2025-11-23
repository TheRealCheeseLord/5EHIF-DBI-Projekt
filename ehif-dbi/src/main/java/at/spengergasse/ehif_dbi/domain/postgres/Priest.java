package at.spengergasse.ehif_dbi.domain.postgres;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
@Table(name = "priest")
public class Priest {

    @EmbeddedId
    private PriestId id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private LocalDate ordinationDate;

    @ManyToMany(mappedBy = "priests")
    @Builder.Default
    private Set<Parish> parishes = new HashSet<>();

    @Embeddable
    public record PriestId(@NotNull @GeneratedValue Long id) {};
}
