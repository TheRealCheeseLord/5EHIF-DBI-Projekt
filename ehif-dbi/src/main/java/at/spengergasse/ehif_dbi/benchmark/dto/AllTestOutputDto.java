package at.spengergasse.ehif_dbi.benchmark.dto;

public record AllTestOutputDto(
        WriteTestOutputDto writeTest,
        ReadTestOutputDto readTest,
        UpdateTestOutputDto updateTest,
        DeleteTestOutputDto deleteTest
) {}
