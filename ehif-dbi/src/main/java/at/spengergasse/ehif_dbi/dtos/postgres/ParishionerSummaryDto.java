package at.spengergasse.ehif_dbi.dtos.postgres;

import at.spengergasse.ehif_dbi.domain.postgres.Parishioner;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ParishionerSummaryDto(
        Long id,
        String firstName,
        String lastName,
        LocalDate birthDate
) {
    public ParishionerSummaryDto(@NotNull Parishioner parishioner) {
        this(
                parishioner.getId().id(),
                parishioner.getFirstName(),
                parishioner.getLastName(),
                parishioner.getBirthDate()
        );
    }
}
