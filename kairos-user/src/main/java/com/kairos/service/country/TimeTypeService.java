package com.kairos.service.country;


import com.kairos.custom_exception.DataNotFoundByIdException;
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
        //int countExisting=timeTypeGraphRepository
        TimeType timeType = timeTypeDTO.buildTimeType();
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
        return  timeTypeGraphRepository.findAllByCountryIdAndEnabled(countryId,true);
    }


}
