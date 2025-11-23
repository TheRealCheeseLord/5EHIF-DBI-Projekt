package at.spengergasse.ehif_dbi.service.mongo;

import at.spengergasse.ehif_dbi.commands.mongo.AddParishionerEmbeddedCommand;
import at.spengergasse.ehif_dbi.commands.mongo.AddPriestEmbeddedCommand;
import at.spengergasse.ehif_dbi.commands.mongo.UpdateParishionerEmbeddedCommand;
import at.spengergasse.ehif_dbi.commands.mongo.UpdatePriestEmbeddedCommand;
import at.spengergasse.ehif_dbi.domain.mongo.ParishDocument;
import at.spengergasse.ehif_dbi.domain.mongo.ParishionerEmbedded;
import at.spengergasse.ehif_dbi.domain.mongo.PriestEmbedded;
import at.spengergasse.ehif_dbi.dtos.mongo.ParishDocumentDto;
import at.spengergasse.ehif_dbi.dtos.mongo.ParishionerEmbeddedDto;
import at.spengergasse.ehif_dbi.dtos.mongo.PriestEmbeddedDto;
import at.spengergasse.ehif_dbi.persistence.mongo.ParishDocumentRepository;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor

@Service
@Transactional(readOnly=true)
public class ParishDocumentService {

    private ParishDocumentRepository parishDocumentRepository;

    public List<ParishDocumentDto> getParishDocuments() {
        return parishDocumentRepository.findAll().stream().map(ParishDocumentDto::new).toList();
    }

    public Optional<ParishDocumentDto> getParishDocumentById(ObjectId id) {
        return parishDocumentRepository.findById(id).map(ParishDocumentDto::new);
    }

    @Transactional(readOnly = false)
    public ParishDocumentDto createParishDocument(ParishDocumentDto parishDocumentDto) {
        return new ParishDocumentDto(parishDocumentRepository.save(ParishDocument.builder()
                        .name(parishDocumentDto.name())
                        .location(parishDocumentDto.location())
                        .foundedYear(parishDocumentDto.foundedYear())
                        .priests(parishDocumentDto.priests().stream().map(priestEmbeddedDto ->
                                PriestEmbedded.builder()
                                        .id(new ObjectId())
                                        .firstName(priestEmbeddedDto.firstName())
                                        .lastName(priestEmbeddedDto.lastName())
                                        .ordinationDate(priestEmbeddedDto.ordinationDate())
                                .build()
                        ).toList())
                        .parishioners(parishDocumentDto.parishioners().stream().map(parisherEmbeddedDto ->
                                ParishionerEmbedded.builder()
                                        .id(new ObjectId())
                                        .firstName(parisherEmbeddedDto.firstName())
                                        .lastName(parisherEmbeddedDto.lastName())
                                        .birthDate(parisherEmbeddedDto.birthDate())
                                .build()
                        ).toList())
                .build()));
    }

    @Transactional(readOnly = false)
    public Optional<ParishDocumentDto> updateParishDocument(ParishDocumentDto parishDocumentDto) {
        return parishDocumentRepository.findById(new ObjectId(parishDocumentDto.id()))
                .map(parish -> {
                    parish.setName(parishDocumentDto.name());
                    parish.setLocation(parishDocumentDto.location());
                    parish.setFoundedYear(parishDocumentDto.foundedYear());
                    parish.setPriests(parishDocumentDto.priests().stream().map(priestEmbeddedDto ->
                            PriestEmbedded.builder()
                                .id(new ObjectId(priestEmbeddedDto.id()))
                                .firstName(priestEmbeddedDto.firstName())
                                .lastName(priestEmbeddedDto.lastName())
                                .ordinationDate(priestEmbeddedDto.ordinationDate())
                            .build()
                    ).toList());
                    parish.setParishioners(parishDocumentDto.parishioners().stream().map(parisherEmbeddedDto ->
                            ParishionerEmbedded.builder()
                                    .id(new ObjectId(parisherEmbeddedDto.id()))
                                    .firstName(parisherEmbeddedDto.firstName())
                                    .lastName(parisherEmbeddedDto.lastName())
                                    .birthDate(parisherEmbeddedDto.birthDate())
                            .build()
                    ).toList());
                    return parishDocumentRepository.save(parish);
                })
                .map(ParishDocumentDto::new);
    }

    @Transactional(readOnly = false)
    public Optional<ParishDocumentDto> deleteParishDocument(ObjectId id) {
        return parishDocumentRepository.findById(id)
                .map(parish -> {
                    parishDocumentRepository.delete(parish);
                    return new ParishDocumentDto(parish);
                });
    }

    public List<PriestEmbeddedDto> getPriests(ObjectId parishDocumentId) {
        return parishDocumentRepository.findById(parishDocumentId)
                .map(parish -> parish.getPriests().stream().map(PriestEmbeddedDto::new).toList())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parish Not Found"));
    }

    public PriestEmbeddedDto getPriestById(ObjectId parishDocumentId, ObjectId priestEmbeddedId) {
        return parishDocumentRepository.findById(parishDocumentId)
                .map(parish -> parish.getPriests().stream()
                        .filter(p -> p.getId().equals(priestEmbeddedId))
                        .findFirst()
                        .map(PriestEmbeddedDto::new)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Priest Not Found")))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parish Not Found"));
    }

    @Transactional(readOnly = false)
    public PriestEmbeddedDto addPriest(ObjectId parishDocumentId, AddPriestEmbeddedCommand addPriestEmbeddedCommand) {
        ParishDocument parishDocument = parishDocumentRepository.findById(parishDocumentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parish Not Found"));

        PriestEmbedded priestEmbedded = PriestEmbedded.builder()
                .id(new ObjectId())
                .firstName(addPriestEmbeddedCommand.firstName())
                .lastName(addPriestEmbeddedCommand.lastName())
                .ordinationDate(addPriestEmbeddedCommand.ordinationDate())
                .build();

        parishDocument.getPriests().add(priestEmbedded);

        parishDocumentRepository.save(parishDocument);
        return new PriestEmbeddedDto(priestEmbedded);
    }

    @Transactional(readOnly = false)
    public PriestEmbeddedDto updatePriest(
            ObjectId parishDocumentId,
            ObjectId priestEmbeddedId,
            UpdatePriestEmbeddedCommand updatePriestEmbeddedCommand
    ) {
        ParishDocument parishDocument = parishDocumentRepository.findById(parishDocumentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parish Not Found"));

        PriestEmbedded priestEmbedded = parishDocument.getPriests().stream()
                .filter(p -> p.getId().equals(priestEmbeddedId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Priest Not Found"));

        priestEmbedded.setFirstName(updatePriestEmbeddedCommand.firstName());
        priestEmbedded.setLastName(updatePriestEmbeddedCommand.lastName());
        priestEmbedded.setOrdinationDate(updatePriestEmbeddedCommand.ordinationDate());

        parishDocumentRepository.save(parishDocument);
        return new PriestEmbeddedDto(priestEmbedded);
    }

    @Transactional(readOnly = false)
    public void removePriest(ObjectId parishDocumentId, ObjectId priestEmbeddedId) {
        ParishDocument parishDocument = parishDocumentRepository.findById(parishDocumentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parish Not Found"));

        parishDocument.getPriests().removeIf(p -> p.getId().equals(priestEmbeddedId));

        parishDocumentRepository.save(parishDocument);
    }


    public List<ParishionerEmbeddedDto> getParishioners(ObjectId parishDocumentId) {
        return parishDocumentRepository.findById(parishDocumentId)
                .map(parish -> parish.getParishioners().stream().map(ParishionerEmbeddedDto::new).toList())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parish Not Found"));
    }

    public ParishionerEmbeddedDto getParishionerById(ObjectId parishDocumentId, ObjectId parishionerEmbeddedId) {
        return parishDocumentRepository.findById(parishDocumentId)
                .map(parish -> parish.getParishioners().stream()
                        .filter(p -> p.getId().equals(parishionerEmbeddedId))
                        .findFirst()
                        .map(ParishionerEmbeddedDto::new)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parishioner Not Found")))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parish Not Found"));
    }

    @Transactional(readOnly = false)
    public ParishionerEmbeddedDto addParishioner(ObjectId parishDocumentId, AddParishionerEmbeddedCommand addParishionerEmbeddedCommand) {
        ParishDocument parishDocument = parishDocumentRepository.findById(parishDocumentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parish Not Found"));

        ParishionerEmbedded parishionerEmbedded = ParishionerEmbedded.builder()
                .id(new ObjectId())
                .firstName(addParishionerEmbeddedCommand.firstName())
                .lastName(addParishionerEmbeddedCommand.lastName())
                .birthDate(addParishionerEmbeddedCommand.birthDate())
                .build();

        parishDocument.getParishioners().add(parishionerEmbedded);

        parishDocumentRepository.save(parishDocument);
        return new ParishionerEmbeddedDto(parishionerEmbedded);
    }

    @Transactional(readOnly = false)
    public ParishionerEmbeddedDto updateParishioner(
            ObjectId parishDocumentId,
            ObjectId parishionerEmbeddedId,
            UpdateParishionerEmbeddedCommand updateParishionerEmbeddedCommand
    ) {
        ParishDocument parishDocument = parishDocumentRepository.findById(parishDocumentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parish Not Found"));

        ParishionerEmbedded parishionerEmbedded = parishDocument.getParishioners().stream()
                .filter(p -> p.getId().equals(parishionerEmbeddedId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parishioner Not Found"));

        parishionerEmbedded.setFirstName(updateParishionerEmbeddedCommand.firstName());
        parishionerEmbedded.setLastName(updateParishionerEmbeddedCommand.lastName());
        parishionerEmbedded.setBirthDate(updateParishionerEmbeddedCommand.birthDate());

        parishDocumentRepository.save(parishDocument);
        return new ParishionerEmbeddedDto(parishionerEmbedded);
    }

    @Transactional(readOnly = false)
    public void removeParishioner(ObjectId parishDocumentId, ObjectId parishionerEmbeddedId) {
        ParishDocument parishDocument = parishDocumentRepository.findById(parishDocumentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parish Not Found"));

        parishDocument.getParishioners().removeIf(p -> p.getId().equals(parishionerEmbeddedId));

        parishDocumentRepository.save(parishDocument);
    }
}
