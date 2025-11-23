package at.spengergasse.ehif_dbi.presentation.api;

import at.spengergasse.ehif_dbi.commands.postgres.AddParishionerCommand;
import at.spengergasse.ehif_dbi.commands.postgres.AddPriestCommand;
import at.spengergasse.ehif_dbi.commands.postgres.CreateParishCommand;
import at.spengergasse.ehif_dbi.commands.postgres.UpdateParishCommand;
import at.spengergasse.ehif_dbi.domain.postgres.Parish;
import at.spengergasse.ehif_dbi.domain.postgres.Parishioner;
import at.spengergasse.ehif_dbi.domain.postgres.Priest;
import at.spengergasse.ehif_dbi.dtos.postgres.ParishDto;
import at.spengergasse.ehif_dbi.dtos.postgres.ParishSummaryDto;
import at.spengergasse.ehif_dbi.dtos.postgres.ParishionerSummaryDto;
import at.spengergasse.ehif_dbi.dtos.postgres.PriestSummaryDto;
import at.spengergasse.ehif_dbi.service.postgres.ParishService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@AllArgsConstructor

@RestController
@RequestMapping(value = "/api/parish", produces = MediaType.APPLICATION_JSON_VALUE)
public class ParishRestController {

    private final ParishService parishService;

    @GetMapping
    public ResponseEntity<List<ParishDto>> getAllParishes() {
        var parishes = parishService.getParishes();

        return (parishes.isEmpty())
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(parishes);
    }

    @GetMapping("summary")
    public ResponseEntity<List<ParishSummaryDto>> getAllParishesSummary() {
        var parishs = parishService.getParishSummaries();

        return (parishs.isEmpty())
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(parishs);
    }

    @GetMapping("{parishId}")
    public ResponseEntity<ParishDto> getParishById(@PathVariable Long parishId) {
        var parish = parishService.getParishById(new Parish.ParishId(parishId));

        return parish.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ParishDto> createParish(@Valid @RequestBody CreateParishCommand createParishCommand) {
        var parish = parishService.createParish(createParishCommand);
        return ResponseEntity.created(
                ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{parishId}")
                        .buildAndExpand(parish.id())
                        .toUri()
        ).body(parish);
    }

    @PutMapping("{parishId}")
    public ResponseEntity<ParishDto> updateParish(
            @PathVariable Long parishId,
            @Valid @RequestBody UpdateParishCommand updateParishCommand
    ) {
        return parishService.updateParish(new Parish.ParishId(parishId), updateParishCommand)
                .map(parishDto -> ResponseEntity.ok().body(parishDto))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("{parishId}")
    public ResponseEntity<ParishDto> deleteParish(@PathVariable Long parishId) {
        return parishService.deleteParish(new Parish.ParishId(parishId))
                .map(parishDto -> ResponseEntity.ok().body(parishDto))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("{parishId}/priests")
    public ResponseEntity<List<PriestSummaryDto>> getPriests(@PathVariable Long parishId) {
        return ResponseEntity.ok(parishService.getPriests(new Parish.ParishId(parishId)));
    }

    @PostMapping("{parishId}/priests")
    public ResponseEntity<PriestSummaryDto> addPriest(
            @PathVariable Long parishId,
            @Valid @RequestBody AddPriestCommand addPriestCommand
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(parishService.addPriest(new Parish.ParishId(parishId), addPriestCommand));
    }

    @DeleteMapping("{parishId}/priests/{priestId}")
    public ResponseEntity<PriestSummaryDto> removePriest(
            @PathVariable Long parishId,
            @PathVariable Long priestId
    ) {
        parishService.removePriest(new Parish.ParishId(parishId), new Priest.PriestId(priestId));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("{parishId}/parishioners")
    public ResponseEntity<List<ParishionerSummaryDto>> getParishioners(@PathVariable Long parishId) {
        return ResponseEntity.ok(parishService.getParishioners(new Parish.ParishId(parishId)));
    }

    @PostMapping("{parishId}/parishioners")
    public ResponseEntity<ParishionerSummaryDto> addParishioner(
            @PathVariable Long parishId,
            @Valid @RequestBody AddParishionerCommand addParishionerCommand
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(parishService.addParishioner(new Parish.ParishId(parishId), addParishionerCommand));
    }

    @DeleteMapping("{parishId}/priests/{parishionerId}")
    public ResponseEntity<ParishionerSummaryDto> removeParishioner(
            @PathVariable Long parishId,
            @PathVariable Long parishionerId
    ) {
        parishService.removeParishioner(new Parish.ParishId(parishId), new Parishioner.ParishionerId(parishionerId));
        return ResponseEntity.noContent().build();
    }
}
