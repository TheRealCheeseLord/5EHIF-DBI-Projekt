package at.spengergasse.ehif_dbi.benchmark;

import at.spengergasse.ehif_dbi.benchmark.dto.*;
import at.spengergasse.ehif_dbi.dtos.mongo.ParishDocumentDto;
import at.spengergasse.ehif_dbi.service.mongo.ParishDocumentSchemaValidator;
import com.networknt.schema.Error;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/benchmarks", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class BenchmarkRestController {

    private final BenchmarkRunner benchmarkRunner;
    private final ParishDocumentSchemaValidator parishDocumentSchemaValidator;

    @Operation(summary = "Run write benchmarks")
    @GetMapping("/writes")
    public ResponseEntity<Map<Integer, WriteTestOutputDto>> runWrites() {
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

    @Operation(summary = "Run mongo find query with/without index comparisons")
    @GetMapping("/mongo-index")
    public ResponseEntity<MongoIndexTestOutputDto> runMongoIndex() {
        return ResponseEntity.ok(benchmarkRunner.runMongoIndexBenchmarks());
    }

    @Operation(summary = "Run aggregation benchmarks")
    @GetMapping("/aggregation")
    public ResponseEntity<AggregationTestOutputDto> runAggregation() {
        return ResponseEntity.ok(benchmarkRunner.runAggregationBenchmarks());
    }

    @Operation(summary = "Validate parishDocument against json schema")
    @PostMapping("/parishDocument-validate")
    public ResponseEntity<List<Error>> validateParishDocument(@RequestBody(required = true) ParishDocumentDto parishDocumentDto) {
        return ResponseEntity.ok(parishDocumentSchemaValidator.validateParishDocumentDto(parishDocumentDto));
    }
}
