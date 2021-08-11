package com.kairos.service.unit_settings;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.commons.utils.ObjectUtils;
import com.kairos.constants.ActivityMessagesConstants;
import com.kairos.dto.activity.unit_settings.ProtectedDaysOffSettingDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.CountryHolidayCalenderDTO;
import com.kairos.enums.ProtectedDaysOffUnitSettings;
import com.kairos.persistence.model.protected_day_off.ProtectedDaysOff;
import com.kairos.persistence.model.unit_settings.ProtectedDaysOffSetting;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.protected_day_off.ProtectedDaysOffRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.unit_settings.ProtectedDaysOffSettingsRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.day_type.CountryHolidayCalenderService;
import com.kairos.service.exception.ExceptionService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Created By G.P.Ranjan on 1/7/19
 **/
@Service
public class ProtectedDaysOffService {
    @Inject
    private ProtectedDaysOffSettingsRepository protectedDaysOffSettingsRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject private ActivityMongoRepository activityMongoRepository;
    @Inject private ShiftMongoRepository shiftMongoRepository;
    @Inject private ProtectedDaysOffRepository protectedDaysOffRepository;
    @Inject private CountryHolidayCalenderService countryHolidayCalenderService;

    @CacheEvict(value = "getProtectedDaysOffByUnitId", key = "#unitId")
    public ProtectedDaysOffSettingDTO saveProtectedDaysOff(Long unitId, ProtectedDaysOffUnitSettings protectedDaysOffUnitSettings){
        ProtectedDaysOffSettingDTO protectedDaysOffSettingDTO = new ProtectedDaysOffSettingDTO(unitId, protectedDaysOffUnitSettings);
        ProtectedDaysOffSetting protectedDaysOffSetting = protectedDaysOffSettingsRepository.getProtectedDaysOffByUnitIdAndDeletedFalse(unitId);
        if(!Optional.ofNullable(protectedDaysOffSetting).isPresent()) {
            protectedDaysOffSetting = new ProtectedDaysOffSetting(protectedDaysOffSettingDTO.getId(), protectedDaysOffSettingDTO.getUnitId(), protectedDaysOffSettingDTO.getProtectedDaysOffUnitSettings());
            protectedDaysOffSettingsRepository.save(protectedDaysOffSetting);
        }
        protectedDaysOffSettingDTO.setId(protectedDaysOffSetting.getId());
        return protectedDaysOffSettingDTO;
    }

    @CacheEvict(value = "getProtectedDaysOffByUnitId", key = "#unitId")
    public ProtectedDaysOffSettingDTO updateProtectedDaysOffByUnitId(Long unitId, ProtectedDaysOffSettingDTO protectedDaysOffSettingDTO){
        ProtectedDaysOffSetting protectedDaysOffSetting = protectedDaysOffSettingsRepository.getProtectedDaysOffByUnitIdAndDeletedFalse(unitId);
        if(!Optional.ofNullable(protectedDaysOffSetting).isPresent()) {
            exceptionService.dataNotFoundException(ActivityMessagesConstants.MESSAGE_ORGANIZATION_PROTECTED_DAYS_OFF, protectedDaysOffSettingDTO.getId());
        }
        protectedDaysOffSetting.setProtectedDaysOffUnitSettings(protectedDaysOffSettingDTO.getProtectedDaysOffUnitSettings());
        protectedDaysOffSettingsRepository.save(protectedDaysOffSetting);
        return ObjectMapperUtils.copyPropertiesByMapper(protectedDaysOffSetting,ProtectedDaysOffSettingDTO.class);
    }

    @Cacheable(value = "getProtectedDaysOffByUnitId", key = "#unitId", cacheManager = "cacheManager")
    public ProtectedDaysOffSettingDTO getProtectedDaysOffByUnitId(Long unitId){
        ProtectedDaysOffSetting protectedDaysOffSetting = protectedDaysOffSettingsRepository.getProtectedDaysOffByUnitIdAndDeletedFalse(unitId);
        if(!Optional.ofNullable(protectedDaysOffSetting).isPresent()) {
            exceptionService.dataNotFoundException(ActivityMessagesConstants.MESSAGE_ORGANIZATION_PROTECTED_DAYS_OFF,unitId);
        }
        return new ProtectedDaysOffSettingDTO(protectedDaysOffSetting.getId(), protectedDaysOffSetting.getUnitId(), protectedDaysOffSetting.getProtectedDaysOffUnitSettings());
    }

    public List<ProtectedDaysOffSettingDTO> getAllProtectedDaysOffByUnitIds(List<Long> unitIds){
        List<ProtectedDaysOffSetting> protectedDaysOffSettings = protectedDaysOffSettingsRepository.getAllProtectedDaysOffByUnitIdsAndDeletedFalse(unitIds);
        return ObjectMapperUtils.copyCollectionPropertiesByMapper(protectedDaysOffSettings,ProtectedDaysOffSettingDTO.class);
    }

    public Boolean createAutoProtectedDaysOffOfAllUnits(Long countryId){
        List<Long> units=userIntegrationService.getUnitIds(countryId);
        units.forEach(unit-> saveProtectedDaysOff(unit,ProtectedDaysOffUnitSettings.ONCE_IN_A_YEAR));
        return true;
    }

    public ProtectedDaysOffSettingDTO addOrUpdateProtectedDaysOffSetting(Long expertiseId, ProtectedDaysOffSettingDTO protectedDaysOffSettingDTO) {
        ProtectedDaysOff protectedDaysOff=ObjectMapperUtils.copyPropertiesByMapper(protectedDaysOffSettingDTO,ProtectedDaysOff.class);
        protectedDaysOff.setExpertiseId(expertiseId);
        protectedDaysOffRepository.save(protectedDaysOff);
        protectedDaysOffSettingDTO.setId(protectedDaysOff.getId());
        return protectedDaysOffSettingDTO;
    }

    public List<ProtectedDaysOffSettingDTO> getProtectedDaysOffByExpertiseId(Long expertiseId){
        return protectedDaysOffRepository.findAllByExpertiseIdAndDeletedFalse(expertiseId);
    }

    public void linkProtectedDaysOffSetting(List<CountryHolidayCalenderDTO> countryHolidayCalendarQueryResults, Set<Long> expertises, Long coutryId) {
        if (ObjectUtils.isCollectionEmpty(expertises)) {
            expertises =userIntegrationService.getAllExpertiseByCountryId(coutryId);
        }
        if (ObjectUtils.isCollectionEmpty(countryHolidayCalendarQueryResults)) {
            countryHolidayCalendarQueryResults = countryHolidayCalenderService.getAllCountryAllHolidaysByCountryId(coutryId);
        }
        List<ProtectedDaysOff> protectedDaysOffs = new ArrayList<>();
        for (CountryHolidayCalenderDTO countryHolidayCalendarQueryResult : countryHolidayCalendarQueryResults) {
            for (Long expertiseId : expertises) {
                if (!countryHolidayCalendarQueryResult.isAllowTimeSettings()) {
                    protectedDaysOffs.add(new ProtectedDaysOff(countryHolidayCalendarQueryResult.getId(), countryHolidayCalendarQueryResult.getHolidayDate(), true, countryHolidayCalendarQueryResult.getDayTypeId(), expertiseId));
                }
            }

        }
        protectedDaysOffRepository.saveEntities(protectedDaysOffs);
    }

    //TODO will get remove after production build
    public boolean transferData(List<ProtectedDaysOffSettingDTO> protectedDaysOffs) {
        List<ProtectedDaysOff> protectedDaysOffList=ObjectMapperUtils.copyCollectionPropertiesByMapper(protectedDaysOffs,ProtectedDaysOff.class);
        protectedDaysOffRepository.saveEntities(protectedDaysOffList);
        return true;
    }



}
