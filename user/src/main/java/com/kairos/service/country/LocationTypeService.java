package com.kairos.service.country;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.LocationType;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.LocationTypeGraphRepository;
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
public class LocationTypeService{

    @Inject
    private LocationTypeGraphRepository locationTypeGraphRepository;

    @Inject
    private CountryGraphRepository countryGraphRepository;

    public Map<String, Object> createLocationType(long countryId, LocationType locationType){
        Country country = countryGraphRepository.findOne(countryId);
        if (country!=null){
            locationType.setCountry(country);
            locationTypeGraphRepository.save(locationType);
            return  locationType.retrieveDetails();
        }
        return null;
    }

    public List<Object> getLocationTypeByCountryId(long countryId){
        List<Map<String,Object>>  data = locationTypeGraphRepository.findLocationTypeByCountry(countryId);
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

    public Map<String, Object> updateLocationType(LocationType locationType){
        LocationType currentLocationType = locationTypeGraphRepository.findOne(locationType.getId());
        if (currentLocationType!=null){
            currentLocationType.setName(locationType.getName());
            currentLocationType.setDescription(locationType.getDescription());
            locationTypeGraphRepository.save(currentLocationType);
            return currentLocationType.retrieveDetails();
        }
        return null;
    }

    public boolean deleteLocationType(long locationTypeId){
        LocationType locationType = locationTypeGraphRepository.findOne(locationTypeId);
        if (locationType!=null){
            locationType.setEnabled(false);
            locationTypeGraphRepository.save(locationType);
            return true;
        }
        return false;
    }
}
