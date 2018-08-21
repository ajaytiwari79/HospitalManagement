package com.kairos.service.country;

import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.HousingType;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.HousingTypeGraphRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by oodles on 9/1/17.
 */
@Service
@Transactional
public class HousingTypeService  {

    @Inject
    private HousingTypeGraphRepository housingTypeGraphRepository;

    @Inject
    private CountryGraphRepository countryGraphRepository;

    public Map<String, Object> createHousingType(long countryId, HousingType housingType){
        Country country = countryGraphRepository.findOne(countryId);
        if (country!=null){
            housingType.setCountry(country);
            housingTypeGraphRepository.save(housingType);
            return housingType.retrieveDetails();
        }
        return null;
    }

    public List<Object> getHousingTypeByCountryId(long countryId){
        List<Map<String,Object>>  data = housingTypeGraphRepository.findHousingTypeByCountry(countryId);
        List<Object> response = new ArrayList<>();;
        if (data!=null){
            for (Map<String,Object> map: data) {
                Object o =  map.get("result");
                response.add(o);
            }
            return response;
        }
        return null;
    }

    public Map<String, Object> updateHousingType(HousingType housingType){
        HousingType currentHousingType = housingTypeGraphRepository.findOne(housingType.getId());
        if (currentHousingType!=null){
            currentHousingType.setName(housingType.getName());
            currentHousingType.setDescription(housingType.getDescription());
            housingTypeGraphRepository.save(currentHousingType);
            return currentHousingType.retrieveDetails();
        }
        return null;
    }

    public boolean deleteHousingType(long kairosStatusId){
        HousingType housingType = housingTypeGraphRepository.findOne(kairosStatusId);
        if (housingType !=null){
            housingType.setEnabled(false);
            housingTypeGraphRepository.save(housingType);
            return true;
        }
        return false;
    }
}
