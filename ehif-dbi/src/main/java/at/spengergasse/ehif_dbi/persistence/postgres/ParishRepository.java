package at.spengergasse.ehif_dbi.persistence.postgres;

import at.spengergasse.ehif_dbi.domain.postgres.Parish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ParishRepository extends JpaRepository<Parish, Parish.ParishId> {
    @Modifying
    @Query(
            value = "UPDATE parish SET name = name || '_UPDATED'",
            nativeQuery = true
    )
    void updateAllParishes();
}
