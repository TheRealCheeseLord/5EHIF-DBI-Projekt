package at.spengergasse.ehif_dbi.presentation.api;

import at.spengergasse.ehif_dbi.commands.mongo.AddParishionerEmbeddedCommand;
import at.spengergasse.ehif_dbi.commands.mongo.AddPriestEmbeddedCommand;
import at.spengergasse.ehif_dbi.commands.mongo.UpdateParishionerEmbeddedCommand;
import at.spengergasse.ehif_dbi.commands.mongo.UpdatePriestEmbeddedCommand;
import at.spengergasse.ehif_dbi.dtos.mongo.ParishDocumentDto;
import at.spengergasse.ehif_dbi.dtos.mongo.ParishionerEmbeddedDto;
import at.spengergasse.ehif_dbi.dtos.mongo.PriestEmbeddedDto;
import at.spengergasse.ehif_dbi.service.mongo.ParishDocumentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@AllArgsConstructor

@RestController
@RequestMapping(value = "/api/parishDocument", produces = MediaType.APPLICATION_JSON_VALUE)
public class ParishDocumentRestController {

    private final ParishDocumentService parishDocumentService;

    @GetMapping
    public ResponseEntity<List<ParishDocumentDto>> getAllParishDocuments() {
        var parishDocumentes = parishDocumentService.getParishDocuments();

        return (parishDocumentes.isEmpty())
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(parishDocumentes);
    }

    @GetMapping("{parishDocumentId}")
    public ResponseEntity<ParishDocumentDto> getParishDocumentById(@PathVariable String parishDocumentId) {
        var parishDocument = parishDocumentService.getParishDocumentById(new ObjectId(parishDocumentId));

        return parishDocument.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ParishDocumentDto> createParishDocument(@Valid @RequestBody ParishDocumentDto parishDocumentDto) {
        var parishDocument = parishDocumentService.createParishDocument(parishDocumentDto);
        return ResponseEntity.created(
                ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{parishDocumentId}")
                        .buildAndExpand(parishDocument.id())
                        .toUri()
        ).body(parishDocument);
    }

    @PutMapping
    public ResponseEntity<ParishDocumentDto> updateParishDocument(@Valid @RequestBody ParishDocumentDto parishDocumentDto) {
        return parishDocumentService.updateParishDocument(parishDocumentDto)
                .map(parish -> ResponseEntity.ok().body(parish))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("{parishDocumentId}")
    public ResponseEntity<ParishDocumentDto> deleteParishDocument(@PathVariable String parishDocumentId) {
        return parishDocumentService.deleteParishDocument(new ObjectId(parishDocumentId))
                .map(parishDocumentDto -> ResponseEntity.ok().body(parishDocumentDto))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("{parishDocumentId}/priests")
    public ResponseEntity<List<PriestEmbeddedDto>> getPriests(@PathVariable String parishDocumentId) {
        return ResponseEntity.ok(parishDocumentService.getPriests(new ObjectId(parishDocumentId)));
    }

    @GetMapping("{parishDocumentId}/priests/{priestEmbeddedId}")
    public ResponseEntity<PriestEmbeddedDto> getPriestById(
            @PathVariable String parishDocumentId,
            @PathVariable String priestEmbeddedId
    ) {
        return ResponseEntity.ok(parishDocumentService.getPriestById(
                new ObjectId(parishDocumentId),
                new ObjectId(priestEmbeddedId))
        );
    }

    @PostMapping("{parishDocumentId}/priests")
    public ResponseEntity<PriestEmbeddedDto> addPriest(
            @PathVariable String parishDocumentId,
            @Valid @RequestBody AddPriestEmbeddedCommand addPriestEmbeddedCommand
    ) {
        return ResponseEntity.ok(parishDocumentService.addPriest(
                new ObjectId(parishDocumentId),
                addPriestEmbeddedCommand
        ));
    }

    @PutMapping("{parishDocumentId}/priests/{priestEmbeddedId}")
    public ResponseEntity<PriestEmbeddedDto> updatePriest(
            @PathVariable String parishDocumentId,
            @PathVariable String priestEmbeddedId,
            @Valid @RequestBody UpdatePriestEmbeddedCommand updatePriestEmbeddedCommand
    ) {
        return ResponseEntity.ok(parishDocumentService.updatePriest(
                new ObjectId(parishDocumentId),
                new ObjectId(priestEmbeddedId),
                updatePriestEmbeddedCommand
        ));
    }

    @DeleteMapping("{parishDocumentId}/priests/{priestEmbeddedId}")
    public ResponseEntity<Void> removePriest(
            @PathVariable String parishDocumentId,
            @PathVariable String priestEmbeddedId
    ) {
        parishDocumentService.removePriest(
                new ObjectId(parishDocumentId),
                new ObjectId(priestEmbeddedId)
        );
        return ResponseEntity.noContent().build();
    }

    @GetMapping("{parishDocumentId}/parishioners")
    public ResponseEntity<List<ParishionerEmbeddedDto>> getParishioners(@PathVariable String parishDocumentId) {
        return ResponseEntity.ok(parishDocumentService.getParishioners(new ObjectId(parishDocumentId)));
    }

    @GetMapping("{parishDocumentId}/parishioners/{parishionerEmbeddedId}")
    public ResponseEntity<ParishionerEmbeddedDto> getParishionerById(
            @PathVariable String parishDocumentId,
            @PathVariable String parishionerEmbeddedId
    ) {
        return ResponseEntity.ok(parishDocumentService.getParishionerById(
                new ObjectId(parishDocumentId),
                new ObjectId(parishionerEmbeddedId))
        );
    }

    @PostMapping("{parishDocumentId}/parishioners")
    public ResponseEntity<ParishionerEmbeddedDto> addParishioner(
            @PathVariable String parishDocumentId,
            @Valid @RequestBody AddParishionerEmbeddedCommand addParishionerEmbeddedCommand
    ) {
        return ResponseEntity.ok(parishDocumentService.addParishioner(
                new ObjectId(parishDocumentId),
                addParishionerEmbeddedCommand
        ));
    }

    @PutMapping("{parishDocumentId}/parishioners/{parishionerEmbeddedId}")
    public ResponseEntity<ParishionerEmbeddedDto> updateParishioner(
            @PathVariable String parishDocumentId,
            @PathVariable String parishionerEmbeddedId,
            @Valid @RequestBody UpdateParishionerEmbeddedCommand updateParishionerEmbeddedCommand
    ) {
        return ResponseEntity.ok(parishDocumentService.updateParishioner(
                new ObjectId(parishDocumentId),
                new ObjectId(parishionerEmbeddedId),
                updateParishionerEmbeddedCommand
        ));
    }

    @DeleteMapping("{parishDocumentId}/parishioners/{parishionerEmbeddedId}")
    public ResponseEntity<Void> removeParishioner(
            @PathVariable String parishDocumentId,
            @PathVariable String parishionerEmbeddedId
    ) {
        parishDocumentService.removeParishioner(
                new ObjectId(parishDocumentId),
                new ObjectId(parishionerEmbeddedId)
        );
        return ResponseEntity.noContent().build();
    }
}
