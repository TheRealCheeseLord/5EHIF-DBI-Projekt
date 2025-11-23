package at.spengergasse.ehif_dbi.dtos.postgres;

import at.spengergasse.ehif_dbi.domain.postgres.Priest;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record PriestDto(
        Long id,
        String firstName,
        String lastName,
        LocalDate ordinationDate,
        List<ParishSummaryDto> parishes
) {
    public PriestDto(@NotNull Priest priest) {
        this(
                priest.getId().id(),
                priest.getFirstName(),
                priest.getLastName(),
                priest.getOrdinationDate(),
                priest.getParishes().stream().map(ParishSummaryDto::new).toList()
        );
    }
}
