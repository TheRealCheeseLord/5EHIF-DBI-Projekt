package at.spengergasse.ehif_dbi.commands.postgres;

import jakarta.validation.constraints.NotNull;

public record UpdateParishCommand(
        @NotNull String name,
        @NotNull String location,
        @NotNull Integer foundedYear
) {}
