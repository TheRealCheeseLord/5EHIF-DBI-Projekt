package at.spengergasse.ehif_dbi.service.postgres;

import at.spengergasse.ehif_dbi.commands.postgres.CreateParishionerCommand;
import at.spengergasse.ehif_dbi.commands.postgres.UpdateParishionerCommand;
import at.spengergasse.ehif_dbi.domain.postgres.Parishioner;
import at.spengergasse.ehif_dbi.dtos.postgres.ParishionerDto;
import at.spengergasse.ehif_dbi.dtos.postgres.ParishionerSummaryDto;
import at.spengergasse.ehif_dbi.persistence.postgres.ParishionerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor

@Service
@Transactional(readOnly=true)
public class ParishionerService {

    private ParishionerRepository parishionerRepository;

    public List<ParishionerDto> getParishioners() {
        return parishionerRepository.findAll().stream().map(ParishionerDto::new).toList();
    }

    public List<ParishionerSummaryDto> getParishionerSummaries() {
        return parishionerRepository.findAll().stream().map(ParishionerSummaryDto::new).toList();
    }

    public Optional<ParishionerDto> getParishionerById(Parishioner.ParishionerId id) {
        return parishionerRepository.findById(id).map(ParishionerDto::new);
    }

    @Transactional(readOnly = false)
    public ParishionerDto createParishioner(CreateParishionerCommand createParishionerCommand) {
        return new ParishionerDto(parishionerRepository.save(Parishioner.builder()
                .firstName(createParishionerCommand.firstName())
                .lastName(createParishionerCommand.lastName())
                .birthDate(createParishionerCommand.birthDate())
                .build()
        ));
    }

    @Transactional(readOnly = false)
    public Optional<ParishionerDto> updateParishioner(Parishioner.ParishionerId parishionerId, UpdateParishionerCommand updateParishionerCommand) {
        return parishionerRepository.findById(parishionerId)
                .map(parishioner -> {
                    parishioner.setFirstName(updateParishionerCommand.firstName());
                    parishioner.setLastName(updateParishionerCommand.lastName());
                    parishioner.setBirthDate(updateParishionerCommand.birthDate());
                    return parishionerRepository.save(parishioner);
                })
                .map(ParishionerDto::new);
    }

    @Transactional(readOnly = false)
    public Optional<ParishionerDto> deleteParishioner(Parishioner.ParishionerId parishionerId) {
        return parishionerRepository.findById(parishionerId)
                .map(parishioner -> {
                    parishionerRepository.delete(parishioner);
                    return new ParishionerDto(parishioner);
                });
    }
}
