package at.spengergasse.ehif_dbi.service.postgres;

import at.spengergasse.ehif_dbi.commands.postgres.CreatePriestCommand;
import at.spengergasse.ehif_dbi.commands.postgres.UpdatePriestCommand;
import at.spengergasse.ehif_dbi.domain.postgres.Priest;
import at.spengergasse.ehif_dbi.dtos.postgres.PriestDto;
import at.spengergasse.ehif_dbi.dtos.postgres.PriestSummaryDto;
import at.spengergasse.ehif_dbi.persistence.postgres.ParishRepository;
import at.spengergasse.ehif_dbi.persistence.postgres.PriestRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor

@Service
@Transactional(readOnly=true)
public class PriestService {

    private PriestRepository priestRepository;
    private ParishRepository parishRepository;

    public List<PriestDto> getPriests() {
        return priestRepository.findAll().stream().map(PriestDto::new).toList();
    }

    public List<PriestSummaryDto> getPriestSummaries() {
        return priestRepository.findAll().stream().map(PriestSummaryDto::new).toList();
    }

    public Optional<PriestDto> getPriestById(Priest.PriestId id) {
        return priestRepository.findById(id).map(PriestDto::new);
    }

    @Transactional(readOnly = false)
    public PriestDto createPriest(CreatePriestCommand createPriestCommand) {
        return new PriestDto(priestRepository.save(Priest.builder()
                .firstName(createPriestCommand.firstName())
                .lastName(createPriestCommand.lastName())
                .ordinationDate(createPriestCommand.ordinationDate())
                .build()
        ));
    }

    @Transactional(readOnly = false)
    public Optional<PriestDto> updatePriest(Priest.PriestId priestId, UpdatePriestCommand updatePriestCommand) {
        return priestRepository.findById(priestId)
                .map(priest -> {
                    priest.setFirstName(updatePriestCommand.firstName());
                    priest.setLastName(updatePriestCommand.lastName());
                    priest.setOrdinationDate(updatePriestCommand.ordinationDate());
                    return priestRepository.save(priest);
                })
                .map(PriestDto::new);
    }

    @Transactional(readOnly = false)
    public Optional<PriestDto> deletePriest(Priest.PriestId priestId) {
        return priestRepository.findById(priestId)
                .map(priest -> {
                    priestRepository.delete(priest);
                    return new PriestDto(priest);
                });
    }
}
