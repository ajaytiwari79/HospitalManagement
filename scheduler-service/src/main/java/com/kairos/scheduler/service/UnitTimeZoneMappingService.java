package com.kairos.scheduler.service;

import com.kairos.scheduler.custom_exception.DataNotFoundByIdException;
import com.kairos.scheduler.persistence.model.unit_settings.UnitTimeZoneMapping;
import com.kairos.scheduler.persistence.repository.UnitTimeZoneMappingRepository;
import com.kairos.scheduler.service.exception.ExceptionService;
import org.springframework.stereotype.Service;
import java.util.Optional;
import javax.inject.Inject;

@Service
public class UnitTimeZoneMappingService extends MongoBaseService {


    @Inject
    private UnitTimeZoneMappingRepository unitTimezoneMappingRepository;
    @Inject
    private ExceptionService exceptionService;

    public UnitTimeZoneMapping createUnitTimezoneMapping(Long unitId, String timezone) {
        UnitTimeZoneMapping unitTimeZoneMapping = unitTimezoneMappingRepository.findByUnitId(unitId);
        if(Optional.ofNullable(unitTimeZoneMapping).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate.unitTimezoneMapping",unitId);
        }

        unitTimeZoneMapping = new UnitTimeZoneMapping(unitId,timezone);
        save(unitTimeZoneMapping);
        return unitTimeZoneMapping;
    }

    public UnitTimeZoneMapping updateUnitTimezoneMapping(String timezone, Long unitId) {

        UnitTimeZoneMapping unitTimeZoneMapping = unitTimezoneMappingRepository.findByUnitId(unitId);
        if(Optional.ofNullable(unitTimeZoneMapping).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.duplicate.unitTimezoneMapping",unitId);
        }
        unitTimeZoneMapping.setTimezone(timezone);
        save(unitTimeZoneMapping);
        return unitTimeZoneMapping;
    }

    public UnitTimeZoneMapping getUnitTimezoneMapping(Long unitId) {

        UnitTimeZoneMapping unitTimeZoneMapping = unitTimezoneMappingRepository.findByUnitId(unitId);
        if(Optional.ofNullable(unitTimeZoneMapping).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.unitTimeZoneMapping.notfound",unitId);
        }
        return unitTimeZoneMapping;
    }
}
