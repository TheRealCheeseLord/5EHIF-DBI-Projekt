package at.spengergasse.ehif_dbi.persistence.mongo;

import at.spengergasse.ehif_dbi.domain.mongo.ParishDocument;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ParishDocumentRepository extends MongoRepository<ParishDocument, ObjectId> {
}
