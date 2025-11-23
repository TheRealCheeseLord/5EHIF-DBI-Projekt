package at.spengergasse.ehif_dbi.persistence.postgres;

import at.spengergasse.ehif_dbi.domain.postgres.Priest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriestRepository extends JpaRepository<Priest, Priest.PriestId> {
}
