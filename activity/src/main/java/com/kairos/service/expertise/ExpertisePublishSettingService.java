package com.kairos.service.expertise;

import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.counter.enums.XAxisConfig;
import com.kairos.dto.activity.night_worker.ExpertiseNightWorkerSettingDTO;
import com.kairos.dto.activity.night_worker.ShiftAndExpertiseNightWorkerSettingDTO;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.activity.shift.StaffEmploymentDetails;
import com.kairos.dto.user.country.time_slot.TimeSlot;
import com.kairos.dto.user.country.time_slot.TimeSlotDTO;
import com.kairos.dto.user.staff.EmploymentDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.DurationType;
import com.kairos.persistence.model.expertise.ExpertisePublishSetting;
import com.kairos.persistence.model.night_worker.ExpertiseNightWorkerSetting;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.repository.expertise.ExpertisePublishSettingRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.time_slot.TimeSlotSetService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.constants.ActivityMessagesConstants.MESSAGE_NIGHTWORKER_SETTING_NOTFOUND;
import static com.kairos.constants.AppConstants.NIGHT;

@Service
@Transactional
public class ExpertisePublishSettingService {

    @Inject
    private ExpertisePublishSettingRepository expertisePublishSettingRepository;
    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject private ShiftMongoRepository shiftMongoRepository;

    public ExpertisePublishSetting getExpertisePublishSettings(Long countryId, Long expertiseId) {
        ExpertisePublishSetting expertisePublishSetting = expertisePublishSettingRepository.findByExpertiseIdAndCountryId(expertiseId, countryId);
        if (!Optional.ofNullable(expertisePublishSetting).isPresent()) {
            expertisePublishSetting = new ExpertisePublishSetting(expertiseId,new HashMap<>(),null,countryId);
        }
        return expertisePublishSetting;
    }

    public ExpertisePublishSetting updateExpertisePublishSettings(Long countryId, Long expertiseId, ExpertisePublishSetting expertisePublishSetting) {
        ExpertisePublishSetting existingExpertisPublishSetting = expertisePublishSettingRepository.findByExpertiseIdAndCountryId(expertiseId, countryId);
        if(isNotNull(existingExpertisPublishSetting)){
            expertisePublishSetting.setId(existingExpertisPublishSetting.getId());
        }
        expertisePublishSettingRepository.save(expertisePublishSetting);
        return expertisePublishSetting;
    }


    public ExpertisePublishSetting updateExpertisePublishSettingsInUnit(Long unitId, Long expertiseId, ExpertisePublishSetting expertisePublishSetting) {
        ExpertisePublishSetting existingExpertisPublishSetting = expertisePublishSettingRepository.findByExpertiseIdAndUnitId(expertiseId,unitId);
        if(isNotNull(existingExpertisPublishSetting)){
            expertisePublishSetting.setId(existingExpertisPublishSetting.getId());
        }
        expertisePublishSettingRepository.save(expertisePublishSetting);
        return expertisePublishSetting;
    }

    public ExpertisePublishSetting getExpertisePublishSettingsForUnit(Long unitId, Long expertiseId) {
        ExpertisePublishSetting expertisePublishSetting = expertisePublishSettingRepository.findByExpertiseIdAndUnitId( expertiseId,unitId);
        if (!Optional.ofNullable(expertisePublishSetting).isPresent()) {
            expertisePublishSetting = expertisePublishSettingRepository.findByExpertiseIdAndCountryId(expertiseId, UserContext.getUserDetails().getCountryId());
            expertisePublishSetting.setId(null);
            expertisePublishSetting.setUnitId(unitId);
            expertisePublishSetting.setCountryId(null);
            expertisePublishSettingRepository.save(expertisePublishSetting);
        }
        return expertisePublishSetting;
    }

    public void updateEmploymentTypeAndExpertiseId(Long unitId){
        List<Map> staffEmploymentDetailsMap = userIntegrationService.getEmploymentAndExpertiseId(unitId);
        shiftMongoRepository.updateEmploymentTypeAndExpertiseId(staffEmploymentDetailsMap);
    }
}
