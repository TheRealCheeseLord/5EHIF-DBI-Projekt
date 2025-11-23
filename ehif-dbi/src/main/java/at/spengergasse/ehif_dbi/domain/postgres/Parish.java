package at.spengergasse.ehif_dbi.domain.postgres;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
@Table(name = "parish")
public class Parish {

    @EmbeddedId
    private ParishId id;

    @Column(nullable = false)
    private String name;

    private String location;

    private Integer foundedYear;

    @ManyToMany
    @JoinTable(
            name = "parish_priest",
            joinColumns = @JoinColumn(name = "parish_id"),
            inverseJoinColumns = @JoinColumn(name = "priest_id")
    )
    @Builder.Default
    private Set<Priest> priests = new HashSet<>();

    @OneToMany(mappedBy = "parish", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Parishioner> parishioners = new ArrayList<>();

    @Embeddable
    public record ParishId(@NotNull @GeneratedValue Long id) {};
}
