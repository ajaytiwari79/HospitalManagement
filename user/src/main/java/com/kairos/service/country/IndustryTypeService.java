package com.kairos.service.country;

import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.IndustryType;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.IndustryTypeGraphRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class IndustryTypeService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    private IndustryTypeGraphRepository industryTypeGraphRepository;

    @Inject
    private CountryGraphRepository countryGraphRepository;

    public Map<String, Object> createIndustryType(long countryId, IndustryType industryType){
        Country country = countryGraphRepository.findOne(countryId);
        if (country!=null){
            industryType.setCountry(country);
            industryTypeGraphRepository.save(industryType);
            return industryType.retrieveDetails();
        }
        return null;
    }

    public List<Object> getIndustryTypeByCountryId(long countryId){
        List<Map<String,Object>>  data = industryTypeGraphRepository.findIndustryTypeByCountry(countryId);
        List<Object> response = new ArrayList<>();
        if (data!=null){
            for (Map<String,Object> map: data) {
                Object o = map.get("result");
                response.add(o);
            }
            return response;
        }
        return null;
    }

    public Map<String, Object> updateIndustryType(IndustryType industryType){
        IndustryType currentIndustryType = industryTypeGraphRepository.findOne(industryType.getId());
        if (currentIndustryType!=null){
            currentIndustryType.setName(industryType.getName());
            currentIndustryType.setDescription(industryType.getDescription());
            industryTypeGraphRepository.save(currentIndustryType);
            return currentIndustryType.retrieveDetails();
        }
        return null;
    }

    public boolean deleteIndustryType(long industryTypeId){
        IndustryType currentIndustryType = industryTypeGraphRepository.findOne(industryTypeId);
        if (currentIndustryType!=null){
            currentIndustryType.setEnabled(false);
            industryTypeGraphRepository.save(currentIndustryType);
            return true;
        }
        return false;
    }
}
