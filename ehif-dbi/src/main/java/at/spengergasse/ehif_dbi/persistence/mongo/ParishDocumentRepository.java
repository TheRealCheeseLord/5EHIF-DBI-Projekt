package at.spengergasse.ehif_dbi.persistence.mongo;

import at.spengergasse.ehif_dbi.domain.mongo.ParishDocument;
import at.spengergasse.ehif_dbi.dtos.mongo.ParishDocumentSummaryDto;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ParishDocumentRepository extends MongoRepository<ParishDocument, ObjectId> {
    List<ParishDocument> findByFoundedYear(Integer foundedYear);
    List<ParishDocument> findByFoundedYearIndexed(Integer foundedYear);

    List<ParishDocument> findAllByFoundedYearBetween(int minFoundedYear, int maxFoundedYear);
    List<ParishDocumentSummaryDto> findAllProjectedByFoundedYearBetween(int minFoundedYear, int maxFoundedYear);
    List<ParishDocumentSummaryDto> findAllProjectedByFoundedYearBetweenOrderByFoundedYearDesc(int minFoundedYear, int maxFoundedYear);
}
