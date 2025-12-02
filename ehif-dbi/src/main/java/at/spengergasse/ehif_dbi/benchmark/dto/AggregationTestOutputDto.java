package at.spengergasse.ehif_dbi.benchmark.dto;

public record AggregationTestOutputDto(
        Long postgresTimeMs,
        Long mongoTimeMs
) {}
