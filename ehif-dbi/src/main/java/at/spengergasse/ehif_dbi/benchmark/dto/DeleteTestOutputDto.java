package at.spengergasse.ehif_dbi.benchmark.dto;

public record DeleteTestOutputDto(
        Long postgresTimeMs,
        Long mongoTimeMs
) {}
