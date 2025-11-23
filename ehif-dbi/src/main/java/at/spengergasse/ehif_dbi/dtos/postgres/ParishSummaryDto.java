package at.spengergasse.ehif_dbi.dtos.postgres;

import at.spengergasse.ehif_dbi.domain.postgres.Parish;
import jakarta.validation.constraints.NotNull;

public record ParishSummaryDto(
        Long id,
        String name,
        String location,
        Integer foundedYear
) {
    public ParishSummaryDto(@NotNull Parish parish) {
        this(
                parish.getId().id(),
                parish.getName(),
                parish.getLocation(),
                parish.getFoundedYear()
        );
    }
}
