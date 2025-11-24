package at.spengergasse.ehif_dbi.benchmark.dto;

public record ReadTestOutputDto(
        Long postgresFindAllTimeMs,
        Long mongoFindAllTimeMs,
        Long postgresWithFilterTimeMs,
        Long mongoWithFilterTimeMs,
        Long postgresWithFilterAndProjectionTimeMs,
        Long mongoWithFilterAndProjectionTimeMs,
        Long postgresWithFilterAndProjectionAndSortTimeMs,
        Long mongoWithFilterAndProjectionAndSortTimeMs
) {}
