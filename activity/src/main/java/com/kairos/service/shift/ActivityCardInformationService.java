package com.kairos.service.shift;

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

    public Map<String,ActivityCardInformation> getActivityCardInformation(Long unitId, Long staffId){
        Map<String,ActivityCardInformation> stringActivityCardInformationMap = new HashMap<String, ActivityCardInformation>(){{
            put("staffActivityCardInformation",activityCardInformationRepository.findByUnitIdAndStaffId(unitId,staffId));
            put("countryActivityCardInformation",activityCardInformationRepository.findByUnitIdAndStaffId(unitId,staffId));
        }};
        return stringActivityCardInformationMap;
    }
}
