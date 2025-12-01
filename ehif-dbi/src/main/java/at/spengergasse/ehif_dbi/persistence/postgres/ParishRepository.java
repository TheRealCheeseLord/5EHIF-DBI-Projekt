package at.spengergasse.ehif_dbi.persistence.postgres;

import at.spengergasse.ehif_dbi.domain.postgres.Parish;
import at.spengergasse.ehif_dbi.dtos.postgres.ParishSummaryDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ParishRepository extends JpaRepository<Parish, Parish.ParishId> {
    @Modifying
    @Query(
            value = "UPDATE parish SET name = name || '_UPDATED'",
            nativeQuery = true
    )
    void updateAllParishes();

    List<Parish> findAllByFoundedYearBetween(int minFoundedYear, int maxFoundedYear);

    @Query("SELECT new at.spengergasse.ehif_dbi.dtos.postgres.ParishSummaryDto(p.id.id, p.name, p.location, p.foundedYear) FROM Parish p")
    List<ParishSummaryDto> findAllProjectedByFoundedYearBetween(int minFoundedYear, int maxFoundedYear);

    @Query("SELECT new at.spengergasse.ehif_dbi.dtos.postgres.ParishSummaryDto(p.id.id, p.name, p.location, p.foundedYear) FROM Parish p")
    List<ParishSummaryDto> findAllProjectedByFoundedYearBetweenOrderByFoundedYearDesc(int minFoundedYear, int maxFoundedYear);
}
