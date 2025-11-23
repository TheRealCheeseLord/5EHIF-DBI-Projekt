package at.spengergasse.ehif_dbi.commands.postgres;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UpdatePriestCommand(
        @NotNull String firstName,
        @NotNull String lastName,
        @NotNull LocalDate ordinationDate
) {}
