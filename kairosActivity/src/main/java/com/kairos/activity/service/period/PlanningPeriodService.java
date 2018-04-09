package com.kairos.activity.service.period;

import com.kairos.activity.constants.AppConstants;
import com.kairos.activity.persistence.model.period.PlanningPeriod;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.activity.service.phase.PhaseService;
import com.kairos.response.dto.web.period.PlanningPeriodDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Created by prerna on 6/4/18.
 */
@Service
@Transactional
public class PlanningPeriodService extends MongoBaseService {
    private static final Logger logger = LoggerFactory.getLogger(PhaseService.class);

    public void createPeriods(Long unitId, PlanningPeriodDTO planningPeriodDTO) {

        PeriodSettings periodSettingsOfParentOrg = null;
        // Set default values
        int presenceLimitInYear = AppConstants.PRESENCE_LIMIT_IN_YEAR;
        int absenceLimitInYear = AppConstants.ABSENCE_LIMIT_IN_YEAR;

        // If parent exists, set settings as of parent
        if(Optional.ofNullable(parentOrgId).isPresent()){
            periodSettingsOfParentOrg = periodSettingsMongoRepository.findByUnit(parentOrgId,false);
        }

        if(Optional.ofNullable(periodSettingsOfParentOrg).isPresent()){
            presenceLimitInYear = periodSettingsOfParentOrg.getPresenceLimitInYear();
            absenceLimitInYear = periodSettingsOfParentOrg.getAbsenceLimitInYear();
        }

        PeriodSettings periodSettings = new PeriodSettings(presenceLimitInYear,absenceLimitInYear, unitId);
        save(periodSettings);
    }
}
