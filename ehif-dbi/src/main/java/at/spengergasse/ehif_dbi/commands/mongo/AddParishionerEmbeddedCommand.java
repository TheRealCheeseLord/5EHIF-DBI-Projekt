package at.spengergasse.ehif_dbi.commands.mongo;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record AddParishionerEmbeddedCommand(
        @NotNull String firstName,
        @NotNull String lastName,
        @NotNull LocalDate birthDate
) {}
