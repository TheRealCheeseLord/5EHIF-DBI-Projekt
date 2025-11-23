package at.spengergasse.ehif_dbi.dtos.mongo;

import at.spengergasse.ehif_dbi.domain.mongo.PriestEmbedded;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record PriestEmbeddedDto(
        String id,
        String firstName,
        String lastName,
        LocalDate ordinationDate
) {
    public PriestEmbeddedDto(@NotNull PriestEmbedded priest) {
        this(
                priest.getId().toString(),
                priest.getFirstName(),
                priest.getLastName(),
                priest.getOrdinationDate()
        );
    }
}
