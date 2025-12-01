package at.spengergasse.ehif_dbi.benchmark;

import at.spengergasse.ehif_dbi.benchmark.dto.*;
import at.spengergasse.ehif_dbi.domain.mongo.ParishDocument;
import at.spengergasse.ehif_dbi.domain.postgres.Parish;
import at.spengergasse.ehif_dbi.dtos.mongo.ParishDocumentSummaryDto;
import at.spengergasse.ehif_dbi.dtos.postgres.ParishSummaryDto;
import at.spengergasse.ehif_dbi.persistence.mongo.ParishDocumentRepository;
import at.spengergasse.ehif_dbi.persistence.postgres.ParishRepository;
import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BenchmarkRunner {

    private final ParishRepository parishRepository;
    private final ParishDocumentRepository parishDocumentRepository;
    private final MongoTemplate mongoTemplate;

    // später kannst du 100_000 ergänzen
    private static final int[] SCALES = {100, 1_000, 100_000};

    // ===========================================================
    // ÖFFENTLICHE METHODEN – werden vom Controller aufgerufen
    // ===========================================================

    /** Nur Writes (inkl. Reset davor) */
    @Transactional()
    public Map<Integer, WriteTestOutputDto> runWriteBenchmarks() {
        System.out.println("=== WRITE BENCHMARKS STARTED ===");

        Map<Integer, WriteTestOutputDto> output = new HashMap<>();

        for (int n : SCALES) {
            System.out.println();
            System.out.println("========== SCALE n = " + n + " ==========");

            output.put(n, runWritesForScale(n));
        }

        System.out.println();
        System.out.println("=== WRITE BENCHMARKS FINISHED ===");

        return output;
    }

    /** Nur Reads – erwartet, dass vorher Daten geschrieben wurden */
    @Transactional(readOnly = true)
    public Map<Integer, ReadTestOutputDto> runReadBenchmarks() {
        System.out.println("=== READ BENCHMARKS STARTED ===");

        Map<Integer, ReadTestOutputDto> output = new HashMap<>();

        for (int n : SCALES) {
            System.out.println();
            System.out.println("NOTE: expects existing data, scale label = " + n);

            output.put(n, runReadsForScale());
        }
        System.out.println();
        System.out.println("=== READ BENCHMARKS FINISHED ===");

        return output;
    }

    /** Nur Updates – erwartet vorhandene Daten */
    @Transactional()
    public UpdateTestOutputDto runUpdateBenchmarks() {
        System.out.println("=== UPDATE BENCHMARKS STARTED ===");

        System.out.println();
        System.out.println("NOTE: expects existing data");

        UpdateTestOutputDto outputDto = runUpdatesForScale();

        System.out.println();
        System.out.println("=== UPDATE BENCHMARKS FINISHED ===");

        return outputDto;
    }

    /** Nur Deletes */
    @Transactional()
    public DeleteTestOutputDto runDeleteBenchmarks() {
        System.out.println("=== DELETE BENCHMARKS STARTED ===");

        System.out.println();
        System.out.println("NOTE: deleteAll");

        DeleteTestOutputDto outputDto = runDeletesForScale();

        System.out.println();
        System.out.println("=== DELETE BENCHMARKS FINISHED ===");

        return outputDto;
    }

    public MongoIndexTestOutputDto runMongoIndexBenchmarks() {
        System.out.println("=== MONGO INDEX BENCHMARKS STARTED ===");

        int approxMaxEntries = Arrays.stream(SCALES).max().getAsInt();

        Random r = new Random();
        int year = 1970 + r.nextInt(approxMaxEntries);

        int iterations = 10;
        long timeNoIndex = 0;
        long timeWithIndex = 0;

        for (int i = 0; i < iterations; i++) {
            timeNoIndex += measureMillis(() -> findMongoWithoutIndex(year));
            timeWithIndex += measureMillis(() -> findMongoWithIndex(year));
        }

        timeNoIndex /= iterations;
        timeWithIndex /= iterations;

        System.out.println("-- FIND: without/with index");
        System.out.println("MongoDB without Index time : " + timeNoIndex + " ms");
        System.out.println("MongoDB with Index time : " + timeWithIndex + " ms");

        System.out.println("=== MONGO INDEX BENCHMARKS FINISHED ===");

        return new MongoIndexTestOutputDto(timeNoIndex, timeWithIndex);
    }

    // ===========================================================
    // PRIVATE HILFSMETHODEN PRO KATEGORIE
    // ===========================================================

    private WriteTestOutputDto runWritesForScale(int n) {
        long pgWrite = measureMillis(() -> writeParishesPostgres(n));
        long mongoWrite = measureMillis(() -> writeParishesMongo(n));

        System.out.println("-- WRITE");
        System.out.println("Postgres write time : " + pgWrite + " ms");
        System.out.println("MongoDB  write time : " + mongoWrite + " ms");

        return new WriteTestOutputDto(pgWrite, mongoWrite);
    }

    private ReadTestOutputDto runReadsForScale() {
        int approxMaxEntries = Arrays.stream(SCALES).max().getAsInt();
        Random r = new Random();
        int maxFoundedYear = r.nextInt(approxMaxEntries);
        int minFoundedYear = maxFoundedYear / 10;

        System.out.println("-- READ: find all");
        long pgReadAll = measureMillis(this::readAllPostgres);
        long mongoReadAll = measureMillis(this::readAllMongo);
        System.out.println("Postgres readAll time : " + pgReadAll + " ms");
        System.out.println("MongoDB  readAll time : " + mongoReadAll + " ms");

        System.out.println("-- READ: with filter (lastName)");
        long pgReadFilter = measureMillis(() -> readFilteredPostgres(minFoundedYear, maxFoundedYear));
        long mongoReadFilter = measureMillis(() -> readFilteredMongo(minFoundedYear, maxFoundedYear));
        System.out.println("Postgres readFiltered time : " + pgReadFilter + " ms");
        System.out.println("MongoDB  readFiltered time : " + mongoReadFilter + " ms");

        System.out.println("-- READ: with filter + projection");
        long pgReadProj = measureMillis(() -> readFilteredProjectedPostgres(minFoundedYear, maxFoundedYear));
        long mongoReadProj = measureMillis(() -> readFilteredProjectedMongo(minFoundedYear, maxFoundedYear));
        System.out.println("Postgres readFiltered+Proj time : " + pgReadProj + " ms");
        System.out.println("MongoDB  readFiltered+Proj time : " + mongoReadProj + " ms");

        System.out.println("-- READ: with filter + projection + sort");
        long pgReadProjSort = measureMillis(() -> readFilteredProjectedSortedPostgres(minFoundedYear, maxFoundedYear));
        long mongoReadProjSort = measureMillis(() -> readFilteredProjectedSortedMongo(minFoundedYear, maxFoundedYear));
        System.out.println("Postgres readFiltered+Proj+Sort time : " + pgReadProjSort + " ms");
        System.out.println("MongoDB  readFiltered+Proj+Sort time : " + mongoReadProjSort + " ms");

        return new ReadTestOutputDto(
                pgReadAll,
                mongoReadAll,
                pgReadFilter,
                mongoReadFilter,
                pgReadProj,
                mongoReadProj,
                pgReadProjSort,
                mongoReadProjSort
        );
    }

    private UpdateTestOutputDto runUpdatesForScale() {
        System.out.println("-- UPDATE (change firstName of some parishioners)");
        long pgUpdate = measureMillis(this::updateAllPostgres);
        long mongoUpdate = measureMillis(this::updateAllMongo);
        System.out.println("Postgres update time : " + pgUpdate + " ms");
        System.out.println("MongoDB  update time : " + mongoUpdate + " ms");

        return new UpdateTestOutputDto(pgUpdate, mongoUpdate);
    }

    private DeleteTestOutputDto runDeletesForScale() {
        System.out.println("-- DELETE ALL");
        long pgDelete = measureMillis(parishRepository::deleteAll);
        long mongoDelete = measureMillis(parishDocumentRepository::deleteAll);
        System.out.println("Postgres deleteAll time : " + pgDelete + " ms");
        System.out.println("MongoDB  deleteAll time : " + mongoDelete + " ms");

        return new DeleteTestOutputDto(pgDelete, mongoDelete);
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

    private void writeParishesPostgres(int n) {
        List<Parish> parish = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            int year = 1970 + i;

            Parish p = Parish.builder()
                    .name("PG_Name_" + i)
                    .location("PG_Location_" + i)
                    .foundedYear(year)
                    .build();

            parish.add(p);
        }

        parishRepository.saveAll(parish);
    }

    private void writeParishesMongo(int n) {
        List<ParishDocument> parishDocuments = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            int year = 1970 + (i % 50);

            ParishDocument parishDoc = ParishDocument.builder()
                    .id(new ObjectId())
                    .name("MG_Name_" + i)
                    .location("MG_Location_" + i)
                    .foundedYear(year)
                    .foundedYearIndexed(year)
                    .build();

            parishDocuments.add(parishDoc);
        }

        parishDocumentRepository.insert(parishDocuments);
    }

    // ===========================================================
    // READ-Helfer
    // ===========================================================

    private List<Parish> readAllPostgres() {
        return parishRepository.findAll();
    }

    private List<ParishDocument> readAllMongo() {
        return parishDocumentRepository.findAll();
    }

    private List<Parish> readFilteredPostgres(int minFoundedYear, int maxFoundedYear) {
        return parishRepository.findAllByFoundedYearBetween(minFoundedYear, maxFoundedYear);
    }

    private List<ParishDocument> readFilteredMongo(int minFoundedYear, int maxFoundedYear) {
        return parishDocumentRepository.findAllByFoundedYearBetween(minFoundedYear, maxFoundedYear);
    }

    private List<ParishSummaryDto> readFilteredProjectedPostgres(int minFoundedYear, int maxFoundedYear) {
        return parishRepository.findAllProjectedByFoundedYearBetween(minFoundedYear, maxFoundedYear);
    }

    private List<ParishDocumentSummaryDto> readFilteredProjectedMongo(int minFoundedYear, int maxFoundedYear) {
        return parishDocumentRepository.findAllProjectedByFoundedYearBetween(minFoundedYear, maxFoundedYear);
    }

    private List<ParishSummaryDto> readFilteredProjectedSortedPostgres(int minFoundedYear, int maxFoundedYear) {
        return parishRepository.findAllProjectedByFoundedYearBetweenOrderByFoundedYearDesc(minFoundedYear, maxFoundedYear);
    }

    private List<ParishDocumentSummaryDto> readFilteredProjectedSortedMongo(int minFoundedYear, int maxFoundedYear) {
        return parishDocumentRepository.findAllProjectedByFoundedYearBetweenOrderByFoundedYearDesc(minFoundedYear, maxFoundedYear);
    }

    // ===========================================================
    // UPDATE
    // ===========================================================

    private void updateAllPostgres() {
        parishRepository.updateAllParishes();
    }

    private void updateAllMongo() {
        Query query = new Query();
        Update update = new Update().set("name", "UPDATED");

        UpdateResult result = mongoTemplate.updateMulti(query, update, ParishDocument.class);
    }

    // ===========================================================
    // MONGO INDEX
    // ===========================================================

    private void findMongoWithoutIndex(int foundedYear) {
        parishDocumentRepository.findByFoundedYear(foundedYear);
    }

    private void findMongoWithIndex(int foundedYear) {
        parishDocumentRepository.findByFoundedYearIndexed(foundedYear);
    }
}
