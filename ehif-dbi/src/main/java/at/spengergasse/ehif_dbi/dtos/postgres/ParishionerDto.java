package at.spengergasse.ehif_dbi.dtos.postgres;

import at.spengergasse.ehif_dbi.domain.postgres.Parishioner;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Optional;

public record ParishionerDto(
        Long id,
        String firstName,
        String lastName,
        LocalDate birthDate,
        ParishSummaryDto parish
) {
    public ParishionerDto(@NotNull Parishioner parishioner) {
        this(
                parishioner.getId().id(),
                parishioner.getFirstName(),
                parishioner.getLastName(),
                parishioner.getBirthDate(),
                Optional.ofNullable(parishioner.getParish())
                        .map(ParishSummaryDto::new)
                        .orElse(null)
        );
    }
}
