package at.spengergasse.ehif_dbi.service.mongo;

import at.spengergasse.ehif_dbi.domain.mongo.ParishDocument;
import at.spengergasse.ehif_dbi.domain.mongo.ParishionerEmbedded;
import at.spengergasse.ehif_dbi.domain.mongo.PriestEmbedded;
import at.spengergasse.ehif_dbi.dtos.mongo.ParishDocumentDto;
import at.spengergasse.ehif_dbi.persistence.mongo.ParishDocumentRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.networknt.schema.Error;
import com.networknt.schema.Schema;
import com.networknt.schema.SchemaRegistry;
import com.networknt.schema.dialect.Dialects;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
@Transactional
public class ParishDocumentSchemaValidator {

    private final Schema schema;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ParishDocumentRepository parishDocumentRepository;

    public ParishDocumentSchemaValidator(ParishDocumentRepository parishDocumentRepository) throws IOException {
        this.parishDocumentRepository = parishDocumentRepository;
        this.objectMapper.registerModule(new JavaTimeModule());
        JsonNode schemaJson = this.objectMapper.readTree(
                new ClassPathResource("schemas/parishDocument-schema.json").getInputStream()
        );
        this.schema = SchemaRegistry
                .withDialect(Dialects.getDraft202012())
                .getSchema(schemaJson);
    }

    public List<Error> validate(Object object) {
        JsonNode node = objectMapper.valueToTree(object);
        return schema.validate(node);
    }

    @Transactional(readOnly = false)
    public List<Error> validateParishDocumentDto(ParishDocumentDto parishDocumentDto) {
        ParishDocument parishDocument = parishDocumentRepository.save(ParishDocument.builder()
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
                .build());

        JsonNode node = objectMapper.valueToTree(parishDocument);
        return schema.validate(node);
    }
}
