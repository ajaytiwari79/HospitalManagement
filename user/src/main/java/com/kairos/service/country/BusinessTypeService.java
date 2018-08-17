package com.kairos.service.country;

import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.default_data.BusinessType;
import com.kairos.persistence.repository.user.country.BusinessTypeGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
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
public class BusinessTypeService {

    @Inject
    private BusinessTypeGraphRepository businessTypeGraphRepository;

    @Inject
    private CountryGraphRepository countryGraphRepository;

    public Map<String, Object> createBusinessType(long countryId, BusinessType businessType){
        Country country = countryGraphRepository.findOne(countryId);
        if (country!=null){
            businessType.setCountry(country);
            businessTypeGraphRepository.save(businessType);
            return businessType.retrieveDetails();
        }
        return null;
    }

    public List<Object> getBusinessTypeByCountryId(long countryId){
        List<Map<String,Object>>  data = businessTypeGraphRepository.findBusinesTypeByCountry(countryId);
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

    public Map<String, Object> updateBusinessType(BusinessType businessType){
        BusinessType currentBusinessType = businessTypeGraphRepository.findOne(businessType.getId());
        if (currentBusinessType!=null){
            currentBusinessType.setName(businessType.getName());
            currentBusinessType.setDescription(businessType.getDescription());
            businessTypeGraphRepository.save(currentBusinessType);
            return currentBusinessType.retrieveDetails();
        }
        return null;
    }

    public boolean deleteBusinessType(long businessTypeId){
        BusinessType businessType = businessTypeGraphRepository.findOne(businessTypeId);
        if (businessType!=null){
            businessType.setEnabled(false);
            businessTypeGraphRepository.save(businessType);
            return true;
        }
        return false;
    }
}
