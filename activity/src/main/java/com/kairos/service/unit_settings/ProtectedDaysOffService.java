package com.kairos.service.unit_settings;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.constants.ActivityMessagesConstants;
import com.kairos.dto.activity.unit_settings.ProtectedDaysOffSettingDTO;
import com.kairos.dto.scheduler.scheduler_panel.SchedulerPanelDTO;
import com.kairos.enums.IntegrationOperation;
import com.kairos.enums.ProtectedDaysOffUnitSettings;
import com.kairos.enums.scheduler.JobSubType;
import com.kairos.enums.scheduler.JobType;
import com.kairos.persistence.model.unit_settings.ProtectedDaysOffSetting;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.unit_settings.ProtectedDaysOffRepository;
import com.kairos.rest_client.RestTemplateResponseEnvelope;
import com.kairos.rest_client.SchedulerServiceRestClient;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import javax.inject.Inject;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import static com.kairos.commons.utils.ObjectUtils.newArrayList;

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
    @Inject
    private SchedulerServiceRestClient schedulerRestClient;

    public ProtectedDaysOffSettingDTO saveProtectedDaysOff(Long unitId, ProtectedDaysOffUnitSettings protectedDaysOffUnitSettings){
        ProtectedDaysOffSettingDTO protectedDaysOffSettingDTO = new ProtectedDaysOffSettingDTO(unitId, protectedDaysOffUnitSettings);
        ProtectedDaysOffSetting protectedDaysOffSetting =protectedDaysOffRepository.getProtectedDaysOffByUnitIdAndDeletedFalse(unitId);
        if(!Optional.ofNullable(protectedDaysOffSetting).isPresent()) {
            protectedDaysOffSetting = new ProtectedDaysOffSetting(protectedDaysOffSettingDTO.getId(), protectedDaysOffSettingDTO.getUnitId(), protectedDaysOffSettingDTO.getProtectedDaysOffUnitSettings());
            protectedDaysOffRepository.save(protectedDaysOffSetting);
        }
        protectedDaysOffSettingDTO.setId(protectedDaysOffSetting.getId());
        return protectedDaysOffSettingDTO;
    }

    public ProtectedDaysOffSettingDTO updateProtectedDaysOffByUnitId(Long unitId, ProtectedDaysOffSettingDTO protectedDaysOffSettingDTO){
        ProtectedDaysOffSetting protectedDaysOffSetting =protectedDaysOffRepository.getProtectedDaysOffByUnitIdAndDeletedFalse(unitId);
        if(!Optional.ofNullable(protectedDaysOffSetting).isPresent()) {
            exceptionService.dataNotFoundException(ActivityMessagesConstants.MESSAGE_ORGANIZATION_PROTECTED_DAYS_OFF, protectedDaysOffSettingDTO.getId());
        }
        protectedDaysOffSetting.setProtectedDaysOffUnitSettings(protectedDaysOffSettingDTO.getProtectedDaysOffUnitSettings());
        protectedDaysOffRepository.save(protectedDaysOffSetting);
        return protectedDaysOffSettingDTO;
    }

    public ProtectedDaysOffSettingDTO getProtectedDaysOffByUnitId(Long unitId){
        ProtectedDaysOffSetting protectedDaysOffSetting =protectedDaysOffRepository.getProtectedDaysOffByUnitIdAndDeletedFalse(unitId);
        if(!Optional.ofNullable(protectedDaysOffSetting).isPresent()) {
            exceptionService.dataNotFoundException(ActivityMessagesConstants.MESSAGE_ORGANIZATION_PROTECTED_DAYS_OFF,unitId);
        }
        return new ProtectedDaysOffSettingDTO(protectedDaysOffSetting.getId(), protectedDaysOffSetting.getUnitId(), protectedDaysOffSetting.getProtectedDaysOffUnitSettings());
    }

    public List<ProtectedDaysOffSettingDTO> getAllProtectedDaysOffByUnitIds(List<Long> unitIds){
        List<ProtectedDaysOffSetting> protectedDaysOffSettings =protectedDaysOffRepository.getAllProtectedDaysOffByUnitIdsAndDeletedFalse(unitIds);
        return ObjectMapperUtils.copyPropertiesOfListByMapper(protectedDaysOffSettings,ProtectedDaysOffSettingDTO.class);
    }

    public Boolean createAutoProtectedDaysOffOfAllUnits(Long countryId){
        List<Long> units=userIntegrationService.getUnitIds(countryId);
        units.forEach(unit-> saveProtectedDaysOff(unit,ProtectedDaysOffUnitSettings.ONCE_IN_A_YEAR));
        return true;
    }


    public void registerJobForProtectedDaysOff() {
        SchedulerPanelDTO schedulerPanelDTO = new SchedulerPanelDTO(newArrayList(DayOfWeek.values()), LocalTime.of(0, 5), JobType.SYSTEM, JobSubType.PROTECTED_DAYS_OFF, ZoneId.systemDefault().toString());
        schedulerRestClient.publishRequest(newArrayList(schedulerPanelDTO), null, false, IntegrationOperation.CREATE, "/scheduler_panel", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<SchedulerPanelDTO>>>() {});
    }

}
