package com.kairos.service.country;

import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.default_data.LocationType;
import com.kairos.persistence.model.country.default_data.LocationTypeDTO;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.LocationTypeGraphRepository;
import com.kairos.service.exception.ExceptionService;
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
public class LocationTypeService {

    @Inject
    private LocationTypeGraphRepository locationTypeGraphRepository;
    @Inject
    private CountryGraphRepository countryGraphRepository;
    @Inject
    private ExceptionService exceptionService;

    public LocationTypeDTO createLocationType(long countryId, LocationTypeDTO locationTypeDTO) {
        Country country = countryGraphRepository.findOne(countryId);
        LocationType locationType = null;
        if (country == null) {
            exceptionService.dataNotFoundByIdException("message.country.id.notFound", countryId);
        } else {
            Boolean locationTypeExistInCountryByName = locationTypeGraphRepository.locationTypeExistInCountryByName(countryId, "(?i)" + locationTypeDTO.getName(), -1L);
            if (locationTypeExistInCountryByName) {
                exceptionService.duplicateDataException("error.LocationType.name.exist");
            }
            locationType = new LocationType(locationTypeDTO.getName(), locationTypeDTO.getDescription());
            locationType.setCountry(country);
            locationTypeGraphRepository.save(locationType);
        }
        locationTypeDTO.setId(locationType.getId());
        return locationTypeDTO;
    }

    public List<LocationTypeDTO> getLocationTypeByCountryId(long countryId) {
        return locationTypeGraphRepository.findLocationTypeByCountry(countryId);
    }

    public LocationTypeDTO updateLocationType(long countryId, LocationTypeDTO locationTypeDTO) {
        Boolean locationTypeExistInCountryByName = locationTypeGraphRepository.locationTypeExistInCountryByName(countryId, "(?i)" + locationTypeDTO.getName(), locationTypeDTO.getId());
        if (locationTypeExistInCountryByName) {
            exceptionService.duplicateDataException("error.LocationType.name.exist");
        }
        LocationType currentLocationType = locationTypeGraphRepository.findOne(locationTypeDTO.getId());
        if (currentLocationType != null) {
            currentLocationType.setName(locationTypeDTO.getName());
            currentLocationType.setDescription(locationTypeDTO.getDescription());
            locationTypeGraphRepository.save(currentLocationType);
        }
        return locationTypeDTO;
    }

    public boolean deleteLocationType(long locationTypeId) {
        LocationType locationType = locationTypeGraphRepository.findOne(locationTypeId);
        if (locationType != null) {
            locationType.setEnabled(false);
            locationTypeGraphRepository.save(locationType);
            return true;
        } else {
            exceptionService.dataNotFoundByIdException("error.LocationType.notfound");
        }
        return true;
    }
}
