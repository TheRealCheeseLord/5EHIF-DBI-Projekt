package at.spengergasse.ehif_dbi.dtos.mongo;

import at.spengergasse.ehif_dbi.domain.mongo.ParishDocument;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ParishDocumentSummaryDto(
        String id,
        String name,
        String location,
        Integer foundedYear
) {
    public ParishDocumentSummaryDto(@NotNull ParishDocument parishDocument) {
        this(
                parishDocument.getId().toString(),
                parishDocument.getName(),
                parishDocument.getLocation(),
                parishDocument.getFoundedYear()
        );
    }
}
