package at.spengergasse.ehif_dbi.presentation.api;

import at.spengergasse.ehif_dbi.commands.postgres.CreateParishionerCommand;
import at.spengergasse.ehif_dbi.commands.postgres.UpdateParishionerCommand;
import at.spengergasse.ehif_dbi.domain.postgres.Parishioner;
import at.spengergasse.ehif_dbi.dtos.postgres.ParishionerDto;
import at.spengergasse.ehif_dbi.dtos.postgres.ParishionerSummaryDto;
import at.spengergasse.ehif_dbi.service.postgres.ParishionerService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@AllArgsConstructor

@RestController
@RequestMapping(value = "/api/parishioner", produces = MediaType.APPLICATION_JSON_VALUE)
public class ParishionerRestController {

    private final ParishionerService parishionerService;

    @GetMapping
    public ResponseEntity<List<ParishionerDto>> getAllParishioners() {
        var parishioners = parishionerService.getParishioners();

        return (parishioners.isEmpty())
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(parishioners);
    }

    @GetMapping("summary")
    public ResponseEntity<List<ParishionerSummaryDto>> getAllParishionersSummary() {
        var parishioners = parishionerService.getParishionerSummaries();

        return (parishioners.isEmpty())
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(parishioners);
    }

    @GetMapping("{parishionerId}")
    public ResponseEntity<ParishionerDto> getParishionerById(@PathVariable Long parishionerId) {
        var parishioner = parishionerService.getParishionerById(new Parishioner.ParishionerId(parishionerId));

        return parishioner.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ParishionerDto> createParishioner(@Valid @RequestBody CreateParishionerCommand createParishionerCommand) {
        var parishioner = parishionerService.createParishioner(createParishionerCommand);
        return ResponseEntity.created(
                ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{parishionerId}")
                        .buildAndExpand(parishioner.id())
                        .toUri()
        ).body(parishioner);
    }

    @PutMapping("{parishionerId}")
    public ResponseEntity<ParishionerDto> updateParishioner(@PathVariable Long parishionerId, @Valid @RequestBody UpdateParishionerCommand updateParishionerCommand) {
        return parishionerService.updateParishioner(new Parishioner.ParishionerId(parishionerId), updateParishionerCommand)
                .map(parishionerDto -> ResponseEntity.ok().body(parishionerDto))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("{parishionerId}")
    public ResponseEntity<ParishionerDto> deleteParishioner(@PathVariable Long parishionerId) {
        return parishionerService.deleteParishioner(new Parishioner.ParishionerId(parishionerId))
                .map(parishionerDto -> ResponseEntity.ok().body(parishionerDto))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
