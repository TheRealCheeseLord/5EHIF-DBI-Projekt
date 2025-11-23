package at.spengergasse.ehif_dbi.domain.mongo;

import lombok.*;
import org.bson.types.ObjectId;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class ParishionerEmbedded {

    private ObjectId id;

    private String firstName;
    private String lastName;
    private LocalDate birthDate;
}
