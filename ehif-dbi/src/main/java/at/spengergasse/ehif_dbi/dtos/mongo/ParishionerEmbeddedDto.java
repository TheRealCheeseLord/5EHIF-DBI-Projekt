package at.spengergasse.ehif_dbi.dtos.mongo;

import at.spengergasse.ehif_dbi.domain.mongo.ParishionerEmbedded;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ParishionerEmbeddedDto(
        String id,
        String firstName,
        String lastName,
        LocalDate birthDate
) {
    public ParishionerEmbeddedDto(@NotNull ParishionerEmbedded parishioner) {
        this(
                parishioner.getId().toString(),
                parishioner.getFirstName(),
                parishioner.getLastName(),
                parishioner.getBirthDate()
        );
    }
}
