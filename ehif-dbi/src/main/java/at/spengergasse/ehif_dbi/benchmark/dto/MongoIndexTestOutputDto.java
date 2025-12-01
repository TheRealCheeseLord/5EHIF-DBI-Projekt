package at.spengergasse.ehif_dbi.benchmark.dto;

public record MongoIndexTestOutputDto(
        Long mongoFindTimeMs,
        Long mongoFindWithIndexTimeMs
) {}
