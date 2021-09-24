package com.kairos.service.period;

import com.kairos.commons.utils.ObjectUtils;
import com.kairos.constants.AppConstants;
import com.kairos.dto.activity.period.PeriodSettingsDTO;
import com.kairos.persistence.model.period.PeriodSettings;
import com.kairos.persistence.repository.period.PeriodSettingsMongoRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Optional;

import static com.kairos.constants.ActivityMessagesConstants.MESSAGE_PERIODSETTING_UNIT;

/**
 * Created by prerna on 30/3/18.
 */
@Service
@Transactional
public class PeriodSettingsService extends MongoBaseService {

    @Inject
    PeriodSettingsMongoRepository periodSettingsMongoRepository;
    @Inject
    ExceptionService exceptionService;

    public PeriodSettings createDefaultPeriodSettings(Long unitId) {

        PeriodSettings periodSettingsOfParentOrg = null;
        // Set default values
        int presenceLimitInYear = AppConstants.PRESENCE_LIMIT_IN_YEAR;
        int absenceLimitInYear = AppConstants.ABSENCE_LIMIT_IN_YEAR;

        if(ObjectUtils.isNotNull(periodSettingsOfParentOrg)){
            presenceLimitInYear = periodSettingsOfParentOrg.getPresenceLimitInYear();
            absenceLimitInYear = periodSettingsOfParentOrg.getAbsenceLimitInYear();
        }

        PeriodSettings periodSettings = new PeriodSettings(presenceLimitInYear,absenceLimitInYear, unitId);
        save(periodSettings);
        return periodSettings;
    }

    public PeriodSettings getPeriodSettings(Long unitId){
        PeriodSettings periodSettings = periodSettingsMongoRepository.findByUnit(unitId);
        if(!Optional.ofNullable(periodSettings).isPresent()){
            periodSettings =  createDefaultPeriodSettings(unitId);
        }
        return periodSettings;
    }

    public PeriodSettings updatePeriodSettings(Long unitId, PeriodSettingsDTO periodSettingsDTO) {
        PeriodSettings periodSettings = periodSettingsMongoRepository.findByUnit(unitId);
        if (!Optional.ofNullable(periodSettings).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_PERIODSETTING_UNIT,unitId);
        }
        periodSettings.setPresenceLimitInYear(periodSettingsDTO.getPresenceLimitInYear());
        periodSettings.setAbsenceLimitInYear(periodSettingsDTO.getAbsenceLimitInYear());

        save(periodSettings);
        return periodSettings;
    }


}
