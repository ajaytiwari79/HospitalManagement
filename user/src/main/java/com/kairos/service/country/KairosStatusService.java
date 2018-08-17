package com.kairos.service.country;

import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.KairosStatus;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.KairosStatusGraphRepository;
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
public class KairosStatusService {

    @Inject
    private KairosStatusGraphRepository kairosStatusGraphRepository;

    @Inject
    private CountryGraphRepository countryGraphRepository;

    public Map<String, Object> createKairosStatus(long countryId, KairosStatus kairosStatus){
        Country country = countryGraphRepository.findOne(countryId);
        if (country!=null){
            kairosStatus.setCountry(country);
            kairosStatusGraphRepository.save(kairosStatus);
            return  kairosStatus.retrieveDetails();
        }
        return null;
    }

    public List<Object> getKairosStatusByCountryId(long countryId){
        List<Map<String,Object>>  data = kairosStatusGraphRepository.findKairosStatusByCountry(countryId);
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

    public Map<String, Object> updateEmployeeLimit(KairosStatus kairosStatus){
        KairosStatus currentKairosStatus = kairosStatusGraphRepository.findOne(kairosStatus.getId());
        if (currentKairosStatus!=null){
            currentKairosStatus.setName(kairosStatus.getName());
            currentKairosStatus.setDescription(kairosStatus.getDescription());
            kairosStatusGraphRepository.save(currentKairosStatus);
            return currentKairosStatus.retrieveDetails();
        }
        return null;
    }

    public boolean deleteKairosStatus(long kairosStatusId){
        KairosStatus kairosStatus = kairosStatusGraphRepository.findOne(kairosStatusId);
        if (kairosStatus !=null){
            kairosStatus.setEnabled(false);
            kairosStatusGraphRepository.save(kairosStatus);
            return true;
        }
        return false;
    }
}
