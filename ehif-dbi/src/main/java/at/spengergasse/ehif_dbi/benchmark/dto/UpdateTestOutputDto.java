package at.spengergasse.ehif_dbi.benchmark.dto;

public record UpdateTestOutputDto(
        Long postgresTimeMs,
        Long mongoTimeMs
) {}
