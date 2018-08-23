package com.kairos.service.country;

import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.default_data.CitizenStatus;
import com.kairos.persistence.repository.user.country.CitizenStatusGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.util.FormatUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

/**
 * Created by oodles on 5/1/17.
 */
@Service
@Transactional
public class CitizenStatusService{

    @Inject
    CitizenStatusGraphRepository citizenStatusGraphRepository;

    @Inject
    CountryGraphRepository countryGraphRepository;

    public Map<String, Object> createCitizenStatus(long countryId, CitizenStatus citizenStatus){
        Country country = countryGraphRepository.findOne(countryId);
        if (country!=null){
            citizenStatus.setCountry(country);
            citizenStatusGraphRepository.save(citizenStatus);
            return citizenStatus.retrieveDetails();
        }
        return  null;
    }

    public Map<String, Object> updateCitizenStatus(CitizenStatus citizenStatus){
        CitizenStatus currentCivilianStatus = citizenStatusGraphRepository.findOne(citizenStatus.getId(),1);
        if (currentCivilianStatus!=null ){
            currentCivilianStatus.setName(citizenStatus.getName());
            currentCivilianStatus.setDescription(citizenStatus.getDescription());
            citizenStatusGraphRepository.save(currentCivilianStatus);
            return currentCivilianStatus.retrieveDetails();
        }
        return  null;
    }


    public boolean deleteCitizenStatus(long citizenStatusId){
        CitizenStatus currentCivilianStatus = citizenStatusGraphRepository.findOne(citizenStatusId);
        if (currentCivilianStatus!=null){
            currentCivilianStatus.setEnabled(false);
            citizenStatusGraphRepository.save(currentCivilianStatus);
            return true;
        }
        return  false;
    }

    public List<Map<String,Object>> getCitizenStatusByCountryId(long countryId){
        List<Map<String, Object>> data = citizenStatusGraphRepository.findCitizenStatusByCountryId(countryId);
        if(data==null){
            return  null;
        }
        return FormatUtil.formatNeoResponse(data);
    }


    public List<Map<String,Object>> getCitizenStatusByCountryIdAnotherFormat(long countryId){
        List<Map<String, Object>> data = citizenStatusGraphRepository.findCitizenStatusByCountryIdAnotherFormat(countryId);
        if(data==null){
          return  null;
        }
        return FormatUtil.formatNeoResponse(data);
    }


}
