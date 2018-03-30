package com.kairos.service.country;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.model.user.country.OwnershipType;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.OwnershipTypeGraphRepository;
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
public class OwnershipTypeService extends UserBaseService {

    @Inject
    private OwnershipTypeGraphRepository ownershipTypeGraphRepository;

    @Inject
    private CountryGraphRepository countryGraphRepository;

    public Map<String, Object> createOwnershipType(long countryId, OwnershipType ownershipType){
        Country country = countryGraphRepository.findOne(countryId);
        if (country!=null){
            ownershipType.setCountry(country);
            save(ownershipType);
            return ownershipType.retrieveDetails();
        }
        return null;
    }

    public List<Object> getOwnershipTypeByCountryId(long countryId){
        List<Map<String,Object>>  data = ownershipTypeGraphRepository.findOwnershipTypeByCountry(countryId);
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

    public Map<String, Object> updateOwnershipType(OwnershipType ownershipType){
        OwnershipType currentOwnershipType = ownershipTypeGraphRepository.findOne(ownershipType.getId());
        if (currentOwnershipType!=null){
            currentOwnershipType.setName(ownershipType.getName());
            currentOwnershipType.setDescription(ownershipType.getDescription());
            save(currentOwnershipType);
            return currentOwnershipType.retrieveDetails();
        }
        return null;
    }

    public boolean deleteOwnershipType(long ownershipTypeId){
        OwnershipType ownershipType = ownershipTypeGraphRepository.findOne(ownershipTypeId);
        if (ownershipType!=null){
            ownershipType.setEnabled(false);
            save(ownershipType);
            return true;
        }
        return false;
    }
}
