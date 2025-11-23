package at.spengergasse.ehif_dbi.commands.postgres;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateParishCommand(
        @NotNull String name,
        @NotNull String location,
        @NotNull Integer foundedYear
) {}
