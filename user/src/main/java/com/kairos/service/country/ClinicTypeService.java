package com.kairos.service.country;
import com.kairos.persistence.model.country.default_data.ClinicType;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.repository.user.country.ClinicTypeGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.service.UserBaseService;
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
public class ClinicTypeService extends UserBaseService {

    @Inject
    private ClinicTypeGraphRepository clinicTypeGraphRepository;
    @Inject
    private CountryGraphRepository countryGraphRepository;



    public Map<String, Object> createClinicType(long countryId, ClinicType clinicType){
        Country country = countryGraphRepository.findOne(countryId);
        if (country!=null){
            clinicType.setCountry(country);
            save(clinicType);
            return  clinicType.retrieveDetails();
        }
        return null;
    }

    public List<Object> getClinicTypeByCountryId(long countryId){
        List<Map<String,Object>>  data = clinicTypeGraphRepository.findClinicByCountryId(countryId);
        List<Object> response =new ArrayList<>();
        if (data!=null){
            for (Map<String,Object> map: data) {
                Object o = map.get("result");
                response.add(o);
            }
            return response;
        }
        return null;
    }

    public Map<String, Object> updateClinicType(ClinicType clinicType){
        ClinicType currentClinicType = clinicTypeGraphRepository.findOne(clinicType.getId());
        if (currentClinicType!=null){
            currentClinicType.setName(clinicType.getName());
            currentClinicType.setDescription(clinicType.getDescription());
            save(currentClinicType);
            return  currentClinicType.retrieveDetails();
        }
        return null;
    }


    public boolean deleteClinicType(long clinicTypeId){
        ClinicType currentClinicType = clinicTypeGraphRepository.findOne(clinicTypeId);
        if (currentClinicType!=null){
            currentClinicType.setEnabled(false);
            save(currentClinicType);
            return true;
        }
        return false;
    }



}
