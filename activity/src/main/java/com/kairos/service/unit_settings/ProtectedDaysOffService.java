package com.kairos.service.unit_settings;

import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.utils.CommonsExceptionUtil;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.constants.ActivityMessagesConstants;
import com.kairos.dto.activity.unit_settings.ProtectedDaysOffSettingDTO;
import com.kairos.enums.ProtectedDaysOffUnitSettings;
import com.kairos.persistence.model.unit_settings.ProtectedDaysOff;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.unit_settings.ProtectedDaysOffRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

import static com.kairos.commons.utils.ObjectUtils.isNull;
import static com.kairos.constants.AppConstants.DAY;

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
    @Inject private ActivityMongoRepository activityMongoRepository;
    @Inject private ShiftMongoRepository shiftMongoRepository;


    public ProtectedDaysOffSettingDTO saveProtectedDaysOff(Long unitId, ProtectedDaysOffUnitSettings protectedDaysOffUnitSettings){
        ProtectedDaysOffSettingDTO protectedDaysOffSettingDTO = new ProtectedDaysOffSettingDTO(unitId, protectedDaysOffUnitSettings);
        ProtectedDaysOff protectedDaysOff =protectedDaysOffRepository.getProtectedDaysOffByUnitIdAndDeletedFalse(unitId);
        if(!Optional.ofNullable(protectedDaysOff).isPresent()) {
            protectedDaysOff = new ProtectedDaysOff(protectedDaysOffSettingDTO.getId(), protectedDaysOffSettingDTO.getUnitId(), protectedDaysOffSettingDTO.getProtectedDaysOffUnitSettings());
            protectedDaysOffRepository.save(protectedDaysOff);
        }
        protectedDaysOffSettingDTO.setId(protectedDaysOff.getId());
        return protectedDaysOffSettingDTO;
    }

    public ProtectedDaysOffSettingDTO updateProtectedDaysOffByUnitId(Long unitId, ProtectedDaysOffSettingDTO protectedDaysOffSettingDTO){
        ProtectedDaysOff protectedDaysOff =protectedDaysOffRepository.getProtectedDaysOffByUnitIdAndDeletedFalse(unitId);
        if(!Optional.ofNullable(protectedDaysOff).isPresent()) {
            exceptionService.dataNotFoundException(ActivityMessagesConstants.MESSAGE_ORGANIZATION_PROTECTED_DAYS_OFF, protectedDaysOffSettingDTO.getId());
        }
        protectedDaysOff.setProtectedDaysOffUnitSettings(protectedDaysOffSettingDTO.getProtectedDaysOffUnitSettings());
        protectedDaysOffRepository.save(protectedDaysOff);
        return ObjectMapperUtils.copyPropertiesByMapper(protectedDaysOff,ProtectedDaysOffSettingDTO.class);
    }

    public ProtectedDaysOffSettingDTO getProtectedDaysOffByUnitId(Long unitId){
        ProtectedDaysOff protectedDaysOff =protectedDaysOffRepository.getProtectedDaysOffByUnitIdAndDeletedFalse(unitId);
        if(!Optional.ofNullable(protectedDaysOff).isPresent()) {
            exceptionService.dataNotFoundException(ActivityMessagesConstants.MESSAGE_ORGANIZATION_PROTECTED_DAYS_OFF,unitId);
        }
        return new ProtectedDaysOffSettingDTO(protectedDaysOff.getId(), protectedDaysOff.getUnitId(), protectedDaysOff.getProtectedDaysOffUnitSettings());
    }

    public List<ProtectedDaysOffSettingDTO> getAllProtectedDaysOffByUnitIds(List<Long> unitIds){
        List<ProtectedDaysOff> protectedDaysOffs =protectedDaysOffRepository.getAllProtectedDaysOffByUnitIdsAndDeletedFalse(unitIds);
        return ObjectMapperUtils.copyCollectionPropertiesByMapper(protectedDaysOffs,ProtectedDaysOffSettingDTO.class);
    }

    public Boolean createAutoProtectedDaysOffOfAllUnits(Long countryId){
        List<Long> units=userIntegrationService.getUnitIds(countryId);
        units.forEach(unit-> saveProtectedDaysOff(unit,ProtectedDaysOffUnitSettings.ONCE_IN_A_YEAR));
        return true;
    }

    public ProtectedDaysOffSettingDTO addOrUpdateProtectedDaysOffSetting(Long expertiseId, ProtectedDaysOffSettingDTO protectedDaysOffSettingDTO) {
        CountryHolidayCalendarQueryResult countryHolidayCalendarQueryResult = countryGraphRepository.findByCalendarHolidayId(protectedDaysOffSettingDTO.getHolidayId());
        if (isNull(countryHolidayCalendarQueryResult)) {
            exceptionService.dataNotMatchedException(MESSAGE_DATANOTFOUND, DAY, DAY_TYPE, protectedDaysOffSettingDTO.getHolidayId());
        }
        protectedDaysOffSettingDTO.setDayTypeId(countryHolidayCalendarQueryResult.getDayType().getId());
        protectedDaysOffSettingDTO.setPublicHolidayDate(countryHolidayCalendarQueryResult.getHolidayDate());
        expertise.getProtectedDaysOffSettings().add(ObjectMapperUtils.copyPropertiesByMapper(protectedDaysOffSettingDTO, ProtectedDaysOffSetting.class));
        expertiseGraphRepository.save(expertise);
        ProtectedDaysOffSetting protectedDaysOffSettings = expertise.getProtectedDaysOffSettings().stream().filter(protectedDaysOffSetting -> protectedDaysOffSetting.getHolidayId().equals(protectedDaysOffSettingDTO.getHolidayId())).findAny().orElse(new ProtectedDaysOffSetting());
        protectedDaysOffSettingDTO.setId(protectedDaysOffSettings.getId());
        return protectedDaysOffSettingDTO;
    }



}
