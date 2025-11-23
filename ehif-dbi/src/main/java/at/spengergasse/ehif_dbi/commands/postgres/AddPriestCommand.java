package at.spengergasse.ehif_dbi.commands.postgres;

import jakarta.validation.constraints.NotNull;

public record AddPriestCommand(@NotNull Long priestId) {}
