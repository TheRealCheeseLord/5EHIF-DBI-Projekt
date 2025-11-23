package at.spengergasse.ehif_dbi.dtos.mongo;

import at.spengergasse.ehif_dbi.domain.mongo.ParishDocument;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ParishDocumentDto(
        String id,
        String name,
        String location,
        Integer foundedYear,
        List<PriestEmbeddedDto> priests,
        List<ParishionerEmbeddedDto> parishioners
) {
    public ParishDocumentDto(@NotNull ParishDocument parishDocument) {
        this(
                parishDocument.getId().toString(),
                parishDocument.getName(),
                parishDocument.getLocation(),
                parishDocument.getFoundedYear(),
                parishDocument.getPriests().stream().map(PriestEmbeddedDto::new).toList(),
                parishDocument.getParishioners().stream().map(ParishionerEmbeddedDto::new).toList()
        );
    }
}
