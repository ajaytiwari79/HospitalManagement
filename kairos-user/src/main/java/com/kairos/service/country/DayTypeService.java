package com.kairos.service.country;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.model.user.country.DayType;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.DayTypeGraphRepository;
import com.kairos.service.UserBaseService;
import com.kairos.util.FormatUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

/**
 * Created by oodles on 9/1/17.
 */
@Service
@Transactional
public class DayTypeService extends UserBaseService {

    @Inject
    private DayTypeGraphRepository dayTypeGraphRepository;
    @Inject
    private CountryGraphRepository countryGraphRepository;

    public Map<String, Object> createDayType(DayType dayType, long countryId){
        Country country = countryGraphRepository.findOne(countryId);
        if (country!=null){
            dayType.setCountry(country);
            save(dayType);
            return dayType.retrieveDetails();
        }
        return null;
    }

    public List<Map<String,Object>> getAllDayTypeByCountryId(long countryId){
        List<Map<String,Object>>  data = dayTypeGraphRepository.findByCountryId(countryId);
        if (data!=null){
         return FormatUtil.formatNeoResponse(data);
        }
        return  null;
    }

    public Map<String, Object> updateDayType(DayType dayType){
        DayType currentDayType = dayTypeGraphRepository.findOne(dayType.getId());
        if (currentDayType!=null){

            currentDayType.setName(dayType.getName());
            currentDayType.setCode(dayType.getCode());
            currentDayType.setColorCode(dayType.getColorCode());
            currentDayType.setDescription(dayType.getDescription());
            save(currentDayType);
            return currentDayType.retrieveDetails();
        }
        return null;
    }

    public boolean deleteDayType(long dayTypeId){
        DayType dayType = dayTypeGraphRepository.findOne(dayTypeId);
        if (dayType!=null){
            dayType.setEnabled(false);
            save(dayType);
            return true;
        }
        return false;
    }

}
