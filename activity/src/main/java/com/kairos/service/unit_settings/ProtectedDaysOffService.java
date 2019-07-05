package com.kairos.service.unit_settings;

import com.kairos.dto.activity.unit_settings.ProtectedDaysOffDTO;
import com.kairos.dto.user.staff.staff.UnitStaffResponseDTO;
import com.kairos.enums.ProtectedDaysOffUnitSettings;
import com.kairos.persistence.model.unit_settings.ProtectedDaysOff;
import com.kairos.persistence.repository.unit_settings.ProtectedDaysOffRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created By G.P.Ranjan on 1/7/19
 **/
@Service
public class ProtectedDaysOffService extends MongoBaseService {
    @Inject
    private ProtectedDaysOffRepository protectedDaysOffRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private UserIntegrationService userIntegrationService;

    public ProtectedDaysOffDTO saveProtectedDaysOff(Long unitId, ProtectedDaysOffUnitSettings protectedDaysOffUnitSettings){
        ProtectedDaysOffDTO protectedDaysOffDTO = new ProtectedDaysOffDTO(unitId, protectedDaysOffUnitSettings);
        ProtectedDaysOff protectedDaysOff=protectedDaysOffRepository.getProtectedDaysOffByUnitIdAndDeletedFalse(unitId);
        if(!Optional.ofNullable(protectedDaysOff).isPresent()) {
            protectedDaysOff = new ProtectedDaysOff(protectedDaysOffDTO.getId(), protectedDaysOffDTO.getUnitId(), protectedDaysOffDTO.getProtectedDaysOffUnitSettings());
            protectedDaysOffRepository.save(protectedDaysOff);
        }
        protectedDaysOffDTO.setId(protectedDaysOff.getId());
        return protectedDaysOffDTO;
    }

    public ProtectedDaysOffDTO updateProtectedDaysOffByUnitId(Long unitId, ProtectedDaysOffDTO protectedDaysOffDTO){
        ProtectedDaysOff protectedDaysOff=protectedDaysOffRepository.getProtectedDaysOffByUnitIdAndDeletedFalse(unitId);
        if(!Optional.ofNullable(protectedDaysOff).isPresent()) {
            exceptionService.dataNotFoundException("Data Not Found ",protectedDaysOffDTO.getId());
        }
        protectedDaysOff.setProtectedDaysOffUnitSettings(protectedDaysOffDTO.getProtectedDaysOffUnitSettings());
        protectedDaysOffRepository.save(protectedDaysOff);
        return protectedDaysOffDTO;
    }

    public ProtectedDaysOffDTO getProtectedDaysOffByUnitId(Long unitId){
        ProtectedDaysOff protectedDaysOff=protectedDaysOffRepository.getProtectedDaysOffByUnitIdAndDeletedFalse(unitId);
        if(!Optional.ofNullable(protectedDaysOff).isPresent()) {
            exceptionService.dataNotFoundException("Data Not Found ",unitId);
        }
        ProtectedDaysOffDTO protectedDaysOffDTO=new ProtectedDaysOffDTO(protectedDaysOff.getId(),protectedDaysOff.getUnitId(),protectedDaysOff.getProtectedDaysOffUnitSettings());
        return protectedDaysOffDTO;
    }

    public List<ProtectedDaysOffDTO> getAllProtectedDaysOffByUnitIds(List<Long> unitIds){
        List<ProtectedDaysOff> protectedDaysOffs=protectedDaysOffRepository.getAllProtectedDaysOffByUnitIdsAndDeletedFalse(unitIds);
        List<ProtectedDaysOffDTO> protectedDaysOffDTOS=new ArrayList<>();
        protectedDaysOffs.forEach(protectedDaysOff -> {
            protectedDaysOffDTOS.add(new ProtectedDaysOffDTO(protectedDaysOff.getId(),protectedDaysOff.getUnitId(),protectedDaysOff.getProtectedDaysOffUnitSettings()));
        });
        return protectedDaysOffDTOS;
    }

    public Boolean createAutoProtectedDaysOffOfAllUnits(Long countryId){
        List<Long> units=userIntegrationService.getUnitIds(countryId);
        units.forEach(unit->{ saveProtectedDaysOff(unit,ProtectedDaysOffUnitSettings.ONCE_IN_A_YEAR);});
        return true;
    }
}
