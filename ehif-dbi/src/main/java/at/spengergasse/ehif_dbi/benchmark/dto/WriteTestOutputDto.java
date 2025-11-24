package at.spengergasse.ehif_dbi.benchmark.dto;

public record WriteTestOutputDto(
        Long postgresTimeMs,
        Long mongoTimeMs
) {}
