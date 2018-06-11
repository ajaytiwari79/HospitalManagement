package com.kairos.activity.service.period;

import com.kairos.activity.constants.AppConstants;
import com.kairos.activity.custom_exception.DataNotFoundByIdException;
import com.kairos.activity.custom_exception.DuplicateDataException;
import com.kairos.activity.persistence.model.period.PeriodSettings;
import com.kairos.activity.persistence.repository.period.PeriodSettingsMongoRepository;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.activity.service.exception.ExceptionService;
import com.kairos.activity.service.phase.PhaseService;
import com.kairos.response.dto.web.period.PeriodSettingsDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by prerna on 30/3/18.
 */
@Service
@Transactional
public class PeriodSettingsService extends MongoBaseService {
    private static final Logger logger = LoggerFactory.getLogger(PhaseService.class);

    @Inject
    PeriodSettingsMongoRepository periodSettingsMongoRepository;
    @Inject
    ExceptionService exceptionService;

    public PeriodSettings createDefaultPeriodSettings(Long unitId) {

        PeriodSettings periodSettingsOfParentOrg = null;
        // Set default values
        int presenceLimitInYear = AppConstants.PRESENCE_LIMIT_IN_YEAR;
        int absenceLimitInYear = AppConstants.ABSENCE_LIMIT_IN_YEAR;

        if(Optional.ofNullable(periodSettingsOfParentOrg).isPresent()){
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
            exceptionService.dataNotFoundByIdException("message.periodsetting.unit",unitId);
        }
        periodSettings.setPresenceLimitInYear(periodSettingsDTO.getPresenceLimitInYear());
        periodSettings.setAbsenceLimitInYear(periodSettingsDTO.getAbsenceLimitInYear());

        save(periodSettings);
        return periodSettings;
    }


}
