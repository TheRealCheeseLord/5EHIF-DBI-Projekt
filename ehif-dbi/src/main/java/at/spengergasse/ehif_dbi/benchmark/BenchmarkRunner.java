package at.spengergasse.ehif_dbi.benchmark;

import at.spengergasse.ehif_dbi.domain.mongo.ParishDocument;
import at.spengergasse.ehif_dbi.domain.mongo.ParishionerEmbedded;
import at.spengergasse.ehif_dbi.domain.postgres.Parishioner;
import at.spengergasse.ehif_dbi.persistence.mongo.ParishDocumentRepository;
import at.spengergasse.ehif_dbi.persistence.postgres.ParishionerRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BenchmarkRunner {

    private final ParishionerRepository parishionerRepository;
    private final ParishDocumentRepository parishDocumentRepository;

    // später kannst du 100_000 ergänzen
    private static final int[] SCALES = {10_000};

    // ===========================================================
    // ÖFFENTLICHE METHODEN – werden vom Controller aufgerufen
    // ===========================================================

    /** Komplettes Szenario: Write + Reads + Update + Delete */
    public void runAllBenchmarks() {
        System.out.println("=== BENCHMARK (ALL) STARTED ===");

        for (int n : SCALES) {
            System.out.println();
            System.out.println("========== SCALE n = " + n + " ==========");

            // DB zurücksetzen
            parishionerRepository.deleteAll();
            parishDocumentRepository.deleteAll();

            runWritesForScale(n);
            runReadsForScale();
            runUpdatesForScale();
            runDeletesForScale();
        }

        System.out.println();
        System.out.println("=== BENCHMARK (ALL) FINISHED ===");
    }

    /** Nur Writes (inkl. Reset davor) */
    public void runWriteBenchmarks() {
        System.out.println("=== WRITE BENCHMARKS STARTED ===");

        for (int n : SCALES) {
            System.out.println();
            System.out.println("========== SCALE n = " + n + " ==========");

            parishionerRepository.deleteAll();
            parishDocumentRepository.deleteAll();

            runWritesForScale(n);
        }

        System.out.println();
        System.out.println("=== WRITE BENCHMARKS FINISHED ===");
    }

    /** Nur Reads – erwartet, dass vorher Daten geschrieben wurden */
    public void runReadBenchmarks() {
        System.out.println("=== READ BENCHMARKS STARTED ===");
        for (int n : SCALES) {
            System.out.println();
            System.out.println("NOTE: expects existing data, scale label = " + n);
            runReadsForScale();
        }
        System.out.println();
        System.out.println("=== READ BENCHMARKS FINISHED ===");
    }

    /** Nur Updates – erwartet vorhandene Daten */
    public void runUpdateBenchmarks() {
        System.out.println("=== UPDATE BENCHMARKS STARTED ===");
        for (int n : SCALES) {
            System.out.println();
            System.out.println("NOTE: expects existing data, scale label = " + n);
            runUpdatesForScale();
        }
        System.out.println();
        System.out.println("=== UPDATE BENCHMARKS FINISHED ===");
    }

    /** Nur Deletes */
    public void runDeleteBenchmarks() {
        System.out.println("=== DELETE BENCHMARKS STARTED ===");
        for (int n : SCALES) {
            System.out.println();
            System.out.println("NOTE: deleteAll – scale label = " + n);
            runDeletesForScale();
        }
        System.out.println();
        System.out.println("=== DELETE BENCHMARKS FINISHED ===");
    }

    // ===========================================================
    // PRIVATE HILFSMETHODEN PRO KATEGORIE
    // ===========================================================

    private void runWritesForScale(int n) {
        long pgWrite = measureMillis(() -> writeParishionersPostgres(n));
        long mongoWrite = measureMillis(() -> writeParishionersMongo(n));

        System.out.println("-- WRITE");
        System.out.println("Postgres write time : " + pgWrite + " ms");
        System.out.println("MongoDB  write time : " + mongoWrite + " ms");
    }

    private void runReadsForScale() {
        System.out.println("-- READ: find all");
        long pgReadAll = measureMillis(this::readAllPostgres);
        long mongoReadAll = measureMillis(this::readAllMongo);
        System.out.println("Postgres readAll time : " + pgReadAll + " ms");
        System.out.println("MongoDB  readAll time : " + mongoReadAll + " ms");

        System.out.println("-- READ: with filter (lastName)");
        long pgReadFilter = measureMillis(this::readFilteredPostgres);
        long mongoReadFilter = measureMillis(this::readFilteredMongo);
        System.out.println("Postgres readFiltered time : " + pgReadFilter + " ms");
        System.out.println("MongoDB  readFiltered time : " + mongoReadFilter + " ms");

        System.out.println("-- READ: with filter + projection");
        long pgReadProj = measureMillis(this::readFilteredProjectedPostgres);
        long mongoReadProj = measureMillis(this::readFilteredProjectedMongo);
        System.out.println("Postgres readFiltered+Proj time : " + pgReadProj + " ms");
        System.out.println("MongoDB  readFiltered+Proj time : " + mongoReadProj + " ms");

        System.out.println("-- READ: with filter + projection + sort");
        long pgReadProjSort = measureMillis(this::readFilteredProjectedSortedPostgres);
        long mongoReadProjSort = measureMillis(this::readFilteredProjectedSortedMongo);
        System.out.println("Postgres readFiltered+Proj+Sort time : " + pgReadProjSort + " ms");
        System.out.println("MongoDB  readFiltered+Proj+Sort time : " + mongoReadProjSort + " ms");
    }

    private void runUpdatesForScale() {
        System.out.println("-- UPDATE (change firstName of some parishioners)");
        long pgUpdate = measureMillis(this::updateSomePostgres);
        long mongoUpdate = measureMillis(this::updateSomeMongo);
        System.out.println("Postgres update time : " + pgUpdate + " ms");
        System.out.println("MongoDB  update time : " + mongoUpdate + " ms");
    }

    private void runDeletesForScale() {
        System.out.println("-- DELETE ALL");
        long pgDelete = measureMillis(parishionerRepository::deleteAll);
        long mongoDelete = measureMillis(parishDocumentRepository::deleteAll);
        System.out.println("Postgres deleteAll time : " + pgDelete + " ms");
        System.out.println("MongoDB  deleteAll time : " + mongoDelete + " ms");
    }

    // ===========================================================
    // GENERISCHES TIMING
    // ===========================================================

    private long measureMillis(Runnable action) {
        long start = System.nanoTime();
        action.run();
        long end = System.nanoTime();
        return (end - start) / 1_000_000; // ns → ms
    }

    // ===========================================================
    // WRITE-OPERATIONEN
    // ===========================================================

    private void writeParishionersPostgres(int n) {
        List<Parishioner> parishioners = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            Parishioner p = Parishioner.builder()
                    .firstName("PG_First_" + i)
                    .lastName("PG_Last_" + (i % 100))  // 0..99 für Filter
                    .birthDate(LocalDate.of(1990, 1, 1).plusDays(i % 365))
                    .build();

            parishioners.add(p);
        }

        parishionerRepository.saveAll(parishioners);
    }

    private void writeParishionersMongo(int n) {
        ParishDocument parishDoc = ParishDocument.builder()
                .id(new ObjectId())
                .name("Benchmark Parish")
                .location("Vienna")
                .foundedYear(1900)
                .build();

        for (int i = 0; i < n; i++) {
            ParishionerEmbedded emb = ParishionerEmbedded.builder()
                    .id(new ObjectId())
                    .firstName("MG_First_" + i)
                    .lastName("MG_Last_" + (i % 100))
                    .birthDate(LocalDate.of(1990, 1, 1).plusDays(i % 365))
                    .build();

            parishDoc.getParishioners().add(emb);
        }

        parishDocumentRepository.save(parishDoc);
    }

    // ===========================================================
    // READ-Helfer
    // ===========================================================

    private List<Parishioner> readAllPostgres() {
        return parishionerRepository.findAll();
    }

    private List<ParishionerEmbedded> readAllMongo() {
        return parishDocumentRepository.findAll()
                .stream()
                .flatMap(doc -> doc.getParishioners().stream())
                .toList();
    }

    private List<Parishioner> readFilteredPostgres() {
        return readAllPostgres().stream()
                .filter(p -> "PG_Last_42".equals(p.getLastName()))
                .toList();
    }

    private List<ParishionerEmbedded> readFilteredMongo() {
        return readAllMongo().stream()
                .filter(p -> "MG_Last_42".equals(p.getLastName()))
                .toList();
    }

    private List<String> readFilteredProjectedPostgres() {
        return readFilteredPostgres().stream()
                .map(p -> p.getFirstName() + " " + p.getLastName())
                .toList();
    }

    private List<String> readFilteredProjectedMongo() {
        return readFilteredMongo().stream()
                .map(p -> p.getFirstName() + " " + p.getLastName())
                .toList();
    }

    private List<String> readFilteredProjectedSortedPostgres() {
        return readFilteredPostgres().stream()
                .sorted(Comparator.comparing(Parishioner::getFirstName))
                .map(p -> p.getFirstName() + " " + p.getLastName())
                .toList();
    }

    private List<String> readFilteredProjectedSortedMongo() {
        return readFilteredMongo().stream()
                .sorted(Comparator.comparing(ParishionerEmbedded::getFirstName))
                .map(p -> p.getFirstName() + " " + p.getLastName())
                .toList();
    }

    // ===========================================================
    // UPDATE
    // ===========================================================

    private void updateSomePostgres() {
        List<Parishioner> all = readAllPostgres();
        int limit = Math.min(50, all.size());

        for (int i = 0; i < limit; i++) {
            Parishioner p = all.get(i);
            p.setFirstName(p.getFirstName() + "_UPDATED");
        }

        parishionerRepository.saveAll(all.subList(0, limit));
    }

    private void updateSomeMongo() {
        List<ParishDocument> docs = parishDocumentRepository.findAll();
        if (docs.isEmpty()) return;

        ParishDocument doc = docs.get(0);
        List<ParishionerEmbedded> parishioners = doc.getParishioners();

        int limit = Math.min(50, parishioners.size());
        for (int i = 0; i < limit; i++) {
            ParishionerEmbedded p = parishioners.get(i);
            p.setFirstName(p.getFirstName() + "_UPDATED");
        }

        parishDocumentRepository.save(doc);
    }
}
