package at.spengergasse.ehif_dbi.benchmark;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/benchmarks")
@RequiredArgsConstructor
public class BenchmarkRestController {

    private final BenchmarkRunner benchmarkRunner;

    @GetMapping("/run-all")
    public String runAll() {
        benchmarkRunner.runAllBenchmarks();
        return "All benchmarks executed. Check server logs.";
    }

    @GetMapping("/writes")
    public String runWrites() {
        benchmarkRunner.runWriteBenchmarks();
        return "Write benchmarks executed. Check server logs.";
    }

    @GetMapping("/reads")
    public String runReads() {
        benchmarkRunner.runReadBenchmarks();
        return "Read benchmarks executed. Check server logs.";
    }

    @GetMapping("/updates")
    public String runUpdates() {
        benchmarkRunner.runUpdateBenchmarks();
        return "Update benchmarks executed. Check server logs.";
    }

    @GetMapping("/deletes")
    public String runDeletes() {
        benchmarkRunner.runDeleteBenchmarks();
        return "Delete benchmarks executed. Check server logs.";
    }
}
