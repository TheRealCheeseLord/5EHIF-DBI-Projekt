package at.spengergasse.ehif_dbi.presentation.api;

import at.spengergasse.ehif_dbi.commands.postgres.CreatePriestCommand;
import at.spengergasse.ehif_dbi.commands.postgres.UpdatePriestCommand;
import at.spengergasse.ehif_dbi.domain.postgres.Priest;
import at.spengergasse.ehif_dbi.dtos.postgres.PriestDto;
import at.spengergasse.ehif_dbi.dtos.postgres.PriestSummaryDto;
import at.spengergasse.ehif_dbi.service.postgres.PriestService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@AllArgsConstructor

@RestController
@RequestMapping(value = "/api/priest", produces = MediaType.APPLICATION_JSON_VALUE)
public class PriestRestController {

    private final PriestService priestService;

    @GetMapping
    public ResponseEntity<List<PriestDto>> getAllPriests() {
        var priests = priestService.getPriests();

        return (priests.isEmpty())
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(priests);
    }

    @GetMapping("summary")
    public ResponseEntity<List<PriestSummaryDto>> getAllPriestsSummary() {
        var priests = priestService.getPriestSummaries();

        return (priests.isEmpty())
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(priests);
    }

    @GetMapping("{priestId}")
    public ResponseEntity<PriestDto> getPriestById(@PathVariable Long priestId) {
        var priest = priestService.getPriestById(new Priest.PriestId(priestId));

        return priest.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PriestDto> createPriest(@Valid @RequestBody CreatePriestCommand createPriestCommand) {
        var priest = priestService.createPriest(createPriestCommand);
        return ResponseEntity.created(
                ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{priestId}")
                        .buildAndExpand(priest.id())
                        .toUri()
        ).body(priest);
    }

    @PutMapping("{priestId}")
    public ResponseEntity<PriestDto> updatePriest(@PathVariable Long priestId, @Valid @RequestBody UpdatePriestCommand updatePriestCommand) {
        return priestService.updatePriest(new Priest.PriestId(priestId), updatePriestCommand)
                .map(priestDto -> ResponseEntity.ok().body(priestDto))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("{priestId}")
    public ResponseEntity<PriestDto> deletePriest(@PathVariable Long priestId) {
        return priestService.deletePriest(new Priest.PriestId(priestId))
                .map(priestDto -> ResponseEntity.ok().body(priestDto))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
