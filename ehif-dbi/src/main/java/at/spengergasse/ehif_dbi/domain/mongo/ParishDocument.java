package at.spengergasse.ehif_dbi.domain.mongo;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Document(collection = "parishes")
public class ParishDocument {

    @Id
    private ObjectId id;

    private String name;
    private String location;
    private Integer foundedYear;

    @Builder.Default
    private List<PriestEmbedded> priests = new ArrayList<>();
    @Builder.Default
    private List<ParishionerEmbedded> parishioners = new ArrayList<>();
}
