package at.spengergasse.ehif_dbi.persistence.postgres;

import at.spengergasse.ehif_dbi.domain.postgres.Parish;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParishRepository extends JpaRepository<Parish, Parish.ParishId> {
}
