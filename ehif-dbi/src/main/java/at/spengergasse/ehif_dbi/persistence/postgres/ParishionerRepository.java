package at.spengergasse.ehif_dbi.persistence.postgres;

import at.spengergasse.ehif_dbi.domain.postgres.Parishioner;
import at.spengergasse.ehif_dbi.domain.postgres.Priest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ParishionerRepository extends JpaRepository<Parishioner, Parishioner.ParishionerId> {}
