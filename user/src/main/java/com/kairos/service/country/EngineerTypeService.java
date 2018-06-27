package com.kairos.service.country;
import com.kairos.user.country.Country;
import com.kairos.user.country.EngineerType;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.EngineerTypeGraphRepository;
import com.kairos.service.UserBaseService;
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
public class EngineerTypeService extends UserBaseService {

    @Inject
    private EngineerTypeGraphRepository engineerTypeGraphRepository;

    @Inject
    private CountryGraphRepository countryGraphRepository;

    public Map<String, Object> createEngineerType(long countryId, EngineerType engineerType){
        Country country = countryGraphRepository.findOne(countryId);
        if (country!=null){
            engineerType.setCountry(country);
            save(engineerType);
           return engineerType.retrieveDetails();
        }
        return null;
    }

    public List<EngineerType> getEngineerTypeByCountryId(long countryId){
        return engineerTypeGraphRepository.findEngineerTypeByCountry(countryId);
    }

    public Map<String, Object> updateEngineerType(EngineerType engineerType){
        EngineerType currentEngineerType = engineerTypeGraphRepository.findOne(engineerType.getId());
        if (currentEngineerType!=null){
            currentEngineerType.setName(engineerType.getName());
            currentEngineerType.setDescription(engineerType.getDescription());
            currentEngineerType.setVisitourCode(engineerType.getVisitourCode());
            engineerTypeGraphRepository.save(currentEngineerType);
            return currentEngineerType.retrieveDetails();
        }
        return null;
    }

    public boolean deleteEngineerType(long engineerTypeId){
        EngineerType engineerType = engineerTypeGraphRepository.findOne(engineerTypeId);
        if (engineerType!=null){
            engineerType.setEnabled(false);
            save(engineerType);
            return true;
        }
        return false;
    }
}
