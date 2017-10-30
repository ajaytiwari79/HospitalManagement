package com.kairos.service.country;


import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.persistence.model.dto.TimeTypeDTO;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.model.user.country.TimeType;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.TimeTypeGraphRepository;
import com.kairos.service.UserBaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

/**
 * Created by vipul on 17/10/17.
 */
@Service
public class TimeTypeService extends UserBaseService {
    private Logger logger = LoggerFactory.getLogger(TimeTypeService.class);
    @Inject
    private CountryGraphRepository countryGraphRepository;
    @Inject
    private TimeTypeGraphRepository timeTypeGraphRepository;

    public TimeTypeDTO addTimeType(TimeTypeDTO timeTypeDTO, long countryId) {
        Country country = countryGraphRepository.findOne(countryId);
        if (!Optional.ofNullable(country).isPresent()) {
            logger.error("Country not found by Id while creating TimeType" + countryId);
            throw new DataNotFoundByIdException("Invalid Country");
        }
        TimeType timeType=timeTypeGraphRepository.findByNameAndTypeIgnoreCase("(?i)"+timeTypeDTO.getName(),"(?i)"+timeTypeDTO.getType());
        if (Optional.ofNullable(timeType).isPresent()) {
            logger.error("Country has already a TimeType with name " + timeTypeDTO.getName()+" and type "+timeTypeDTO.getType());
            throw new DuplicateDataException("Country has already a TimeType with name and type");
        }
        timeType = timeTypeDTO.buildTimeType();
        timeType.setCountry(country);
        save(timeType);
        timeTypeDTO.setId(timeType.getId());
        return timeTypeDTO;
    }
    public List<TimeTypeDTO> getAllTimeTypes(Long countryId){
        Country country = countryGraphRepository.findOne(countryId);
        if (!Optional.ofNullable(country).isPresent()) {
            logger.error("Country not found by Id while creating TimeType" + countryId);
            throw new DataNotFoundByIdException("Invalid Country");
        }
        return  timeTypeGraphRepository.findAllByCountryId(countryId);
    }
    public void deleteTimeType(long timeTypeId){
        TimeType timeType = timeTypeGraphRepository.findOne(timeTypeId);
        if (!Optional.ofNullable(timeType).isPresent()) {
            logger.error("TimeType does not exist" + timeTypeId);
            throw new DataNotFoundByIdException("Invalid timeType");
        }
        timeType.setEnabled(false);
        save(timeType);
    }

    public TimeTypeDTO updateTimeType(TimeTypeDTO timeTypeDTO, long countryId){
        TimeType timeType = timeTypeGraphRepository.findOne(timeTypeDTO.getId());
        if (!Optional.ofNullable(timeType).isPresent()) {
            logger.error("TimeType does not exist" + timeTypeDTO.getId());
            throw new DataNotFoundByIdException("Invalid timeType");
        }
        timeType.setType(timeTypeDTO.getType());
        timeType.setName(timeTypeDTO.getName());
        timeType.setIncludeInTimeBank(timeTypeDTO.isIncludeInTimeBank());
        timeType.setNegativeDayBalancePresent(timeTypeDTO.getNegativeDayBalancePresent());
        timeType.setOnCallTime(timeTypeDTO.getOnCallTime());
        save(timeType);
        return timeTypeDTO;
    }


}
