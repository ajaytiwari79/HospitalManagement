package com.kairos.service.country;


import com.kairos.persistence.model.dto.TimeTypeDTO;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.model.user.country.TimeType;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.service.UserBaseService;

import javax.inject.Inject;

/**
 * Created by vipul on 17/10/17.
 */
public class TimeTypeService extends UserBaseService {
    @Inject
    private CountryGraphRepository countryGraphRepository;

    public TimeType addTimeType(TimeTypeDTO timeTypeDTO, long countryId){
        Country country = countryGraphRepository.findOne(countryId);
        TimeType timeType= timeTypeDTO.buildTimeType();
        return  timeType;

    }
}
