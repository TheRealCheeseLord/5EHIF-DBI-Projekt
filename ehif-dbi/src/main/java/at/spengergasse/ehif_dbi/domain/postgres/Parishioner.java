package at.spengergasse.ehif_dbi.domain.postgres;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
@Table(name = "parishioner")
public class Parishioner {

    @EmbeddedId
    private ParishionerId id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private LocalDate birthDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parish_id")
    private Parish parish;

    @Embeddable
    public record ParishionerId(@NotNull @GeneratedValue Long id) {};
}
