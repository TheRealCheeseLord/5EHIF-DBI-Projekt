package at.spengergasse.ehif_dbi.benchmark;

import at.spengergasse.ehif_dbi.benchmark.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/benchmarks", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class BenchmarkRestController {

    private final BenchmarkRunner benchmarkRunner;

    /**
     * WARNING: This endpoint is primarily for convenience and is NOT recommended for accurate benchmarking.
     * Each benchmark (writes, reads, updates, deletes) should be run individually via their own endpoints
     * and aggregated externally for correct timing measurements.
     */
    @Operation(
            summary = "Run all benchmarks (not recommended)",
            description = "This endpoint calls all benchmarks in one request. "
                    + "For accurate timing, each benchmark should be called individually "
                    + "and results aggregated externally.",
            deprecated = true
    )
    @GetMapping("/run-all")
    public ResponseEntity<AllTestOutputDto> runAll() {
        return ResponseEntity.ok(benchmarkRunner.runAllBenchmarks());
    }

    @Operation(summary = "Run write benchmarks")
    @GetMapping("/writes")
    public ResponseEntity<WriteTestOutputDto> runWrites() {
        return ResponseEntity.ok(benchmarkRunner.runWriteBenchmarks());
    }

    @Operation(summary = "Run read benchmarks")
    @GetMapping("/reads")
    public ResponseEntity<ReadTestOutputDto> runReads() {
        return ResponseEntity.ok(benchmarkRunner.runReadBenchmarks());
    }

    @Operation(summary = "Run update benchmarks")
    @GetMapping("/updates")
    public ResponseEntity<UpdateTestOutputDto> runUpdates() {
        return ResponseEntity.ok(benchmarkRunner.runUpdateBenchmarks());
    }

    @Operation(summary = "Run delete benchmarks")
    @GetMapping("/deletes")
    public ResponseEntity<DeleteTestOutputDto> runDeletes() {
        return ResponseEntity.ok(benchmarkRunner.runDeleteBenchmarks());
    }
}
