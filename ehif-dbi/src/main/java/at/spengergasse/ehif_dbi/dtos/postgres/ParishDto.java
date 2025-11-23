package at.spengergasse.ehif_dbi.dtos.postgres;

import at.spengergasse.ehif_dbi.domain.postgres.Parish;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ParishDto(
        Long id,
        String name,
        String location,
        Integer foundedYear,
        List<PriestSummaryDto> priests,
        List<ParishionerSummaryDto> parishioners
) {
    public ParishDto(@NotNull Parish parish) {
        this(
                parish.getId().id(),
                parish.getName(),
                parish.getLocation(),
                parish.getFoundedYear(),
                parish.getPriests().stream().map(PriestSummaryDto::new).toList(),
                parish.getParishioners().stream().map(ParishionerSummaryDto::new).toList()
        );
    }
}
