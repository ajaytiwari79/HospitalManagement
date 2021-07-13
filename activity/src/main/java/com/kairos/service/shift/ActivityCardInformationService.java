package com.kairos.service.shift;

import com.kairos.enums.shift.ViewType;
import com.kairos.persistence.repository.shift.ActivityCardInformationRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@Service
public class ActivityCardInformationService {

    @Inject private ActivityCardInformationRepository activityCardInformationRepository;

    public ActivityCardInformation updateActivityCardInformation(ActivityCardInformation activityCardInformation) {
        return activityCardInformationRepository.save(activityCardInformation);
    }

    public Map<String,ActivityCardInformation> getActivityCardInformation(Long unitId, Long staffId, ViewType viewType){
        Map<String,ActivityCardInformation> stringActivityCardInformationMap = new HashMap<String, ActivityCardInformation>(){{
            put("staffActivityCardInformation",activityCardInformationRepository.findByUnitIdAndStaffId(unitId,staffId,viewType));
            put("countryActivityCardInformation",activityCardInformationRepository.findByUnitIdAndCountryAdminSetting(unitId,true,viewType));
        }};
        return stringActivityCardInformationMap;
    }
}
