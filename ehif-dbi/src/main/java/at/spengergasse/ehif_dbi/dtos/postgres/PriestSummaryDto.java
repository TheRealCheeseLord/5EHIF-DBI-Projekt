package at.spengergasse.ehif_dbi.dtos.postgres;

import at.spengergasse.ehif_dbi.domain.postgres.Priest;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record PriestSummaryDto(
        Long id,
        String firstName,
        String lastName,
        LocalDate ordinationDate
) {
    public PriestSummaryDto(@NotNull Priest priest) {
        this(
                priest.getId().id(),
                priest.getFirstName(),
                priest.getLastName(),
                priest.getOrdinationDate()
        );
    }
}
