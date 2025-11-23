package at.spengergasse.ehif_dbi.service.postgres;

import at.spengergasse.ehif_dbi.commands.postgres.AddParishionerCommand;
import at.spengergasse.ehif_dbi.commands.postgres.AddPriestCommand;
import at.spengergasse.ehif_dbi.commands.postgres.CreateParishCommand;
import at.spengergasse.ehif_dbi.commands.postgres.UpdateParishCommand;
import at.spengergasse.ehif_dbi.domain.postgres.Parish;
import at.spengergasse.ehif_dbi.domain.postgres.Parishioner;
import at.spengergasse.ehif_dbi.domain.postgres.Priest;
import at.spengergasse.ehif_dbi.dtos.postgres.ParishDto;
import at.spengergasse.ehif_dbi.dtos.postgres.ParishSummaryDto;
import at.spengergasse.ehif_dbi.dtos.postgres.ParishionerSummaryDto;
import at.spengergasse.ehif_dbi.dtos.postgres.PriestSummaryDto;
import at.spengergasse.ehif_dbi.persistence.postgres.ParishRepository;
import at.spengergasse.ehif_dbi.persistence.postgres.ParishionerRepository;
import at.spengergasse.ehif_dbi.persistence.postgres.PriestRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor

@Service
@Transactional(readOnly=true)
public class ParishService {

    private ParishRepository parishRepository;
    private PriestRepository priestRepository;
    private ParishionerRepository parishionerRepository;

    public List<ParishDto> getParishes() {
        return parishRepository.findAll().stream().map(ParishDto::new).toList();
    }

    public List<ParishSummaryDto> getParishSummaries() {
        return parishRepository.findAll().stream().map(ParishSummaryDto::new).toList();
    }

    public Optional<ParishDto> getParishById(Parish.ParishId id) {
        return parishRepository.findById(id).map(ParishDto::new);
    }

    @Transactional(readOnly = false)
    public ParishDto createParish(CreateParishCommand createParishCommand) {
        return new ParishDto(parishRepository.save(Parish.builder()
                .name(createParishCommand.name())
                .location(createParishCommand.location())
                .foundedYear(createParishCommand.foundedYear())
                .build()
        ));
    }

    @Transactional(readOnly = false)
    public Optional<ParishDto> updateParish(Parish.ParishId parishId, UpdateParishCommand updateParishCommand) {
        return parishRepository.findById(parishId)
                .map(parish -> {
                    parish.setName(updateParishCommand.name());
                    parish.setLocation(updateParishCommand.location());
                    parish.setFoundedYear(updateParishCommand.foundedYear());
                    return parishRepository.save(parish);
                })
                .map(ParishDto::new);
    }

    @Transactional(readOnly = false)
    public Optional<ParishDto> deleteParish(Parish.ParishId parishId) {
        return parishRepository.findById(parishId)
                .map(parish -> {
                    parishRepository.delete(parish);
                    return new ParishDto(parish);
                });
    }

    public List<PriestSummaryDto> getPriests(Parish.ParishId parishId) {
        return parishRepository.findById(parishId)
                .map(parish -> parish.getPriests().stream().map(PriestSummaryDto::new).toList())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parish Not Found"));
    }

    @Transactional(readOnly = false)
    public PriestSummaryDto addPriest(Parish.ParishId parishId, AddPriestCommand addPriestCommand) {
        Parish parish = parishRepository.findById(parishId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parish Not Found"));

        Priest priest = priestRepository.findById(new Priest.PriestId(addPriestCommand.priestId()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Priest Not Found"));

        parish.getPriests().add(priest);

        return new PriestSummaryDto(priest);
    }

    @Transactional(readOnly = false)
    public void removePriest(Parish.ParishId parishId, Priest.PriestId priestId) {
        Parish parish = parishRepository.findById(parishId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parish Not Found"));

        Priest priest = priestRepository.findById(priestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Priest Not Found"));

        parish.getPriests().remove(priest);
    }

    public List<ParishionerSummaryDto> getParishioners(Parish.ParishId parishId) {
        return parishRepository.findById(parishId)
                .map(parish -> parish.getParishioners().stream().map(ParishionerSummaryDto::new).toList())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parish Not Found"));
    }

    @Transactional(readOnly = false)
    public ParishionerSummaryDto addParishioner(Parish.ParishId parishId, AddParishionerCommand addParishionerCommand) {
        Parish parish = parishRepository.findById(parishId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parish Not Found"));

        Parishioner parishioner = parishionerRepository.findById(new Parishioner.ParishionerId(addParishionerCommand.parishionerId()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parishioner Not Found"));

        parishioner.setParish(parish);
        parish.getParishioners().add(parishioner);
        parishionerRepository.save(parishioner);

        return new ParishionerSummaryDto(parishioner);
    }

    @Transactional(readOnly = false)
    public void removeParishioner(Parish.ParishId parishId, Parishioner.ParishionerId parishionerId) {
        Parish parish = parishRepository.findById(parishId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parish Not Found"));

        Parishioner parishioner = parishionerRepository.findById(parishionerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parishioner Not Found"));

        if (!parishioner.getParish().getId().equals(parishId)) {
            throw new IllegalArgumentException("Parishioner not associated with Parish");
        }

        parishioner.setParish(null);
        parish.getParishioners().remove(parishioner);

        parishionerRepository.save(parishioner);
    }
}
