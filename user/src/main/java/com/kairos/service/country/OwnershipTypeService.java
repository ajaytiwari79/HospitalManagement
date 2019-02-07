package com.kairos.service.country;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.default_data.OwnershipType;
import com.kairos.persistence.model.country.default_data.OwnershipTypeDTO;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.OwnershipTypeGraphRepository;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by oodles on 9/1/17.
 */
@Service
@Transactional
public class OwnershipTypeService {

    @Inject
    private OwnershipTypeGraphRepository ownershipTypeGraphRepository;
    @Inject
    private CountryGraphRepository countryGraphRepository;
    @Inject
    private ExceptionService exceptionService;

    public OwnershipTypeDTO createOwnershipType(long countryId, OwnershipTypeDTO ownershipTypeDTO){
        Country country = countryGraphRepository.findOne(countryId);
        OwnershipType ownershipType = null;
        if ( country == null) {
            exceptionService.dataNotFoundByIdException("message.country.id.notFound", countryId);
        } else {
            Boolean ownershipTypeExistInCountryByName = ownershipTypeGraphRepository.ownershipTypeExistInCountryByName(countryId, "(?i)" + ownershipTypeDTO.getName(), -1L);
            if (ownershipTypeExistInCountryByName) {
                exceptionService.duplicateDataException("error.OwnershipType.name.exist");
            }
            ownershipType = new OwnershipType(ownershipTypeDTO.getName(), ownershipTypeDTO.getDescription());
            ownershipType.setCountry(country);
            ownershipTypeGraphRepository.save(ownershipType);
        }
        ownershipTypeDTO.setId(ownershipType.getId());
        return ownershipTypeDTO;
    }

    public List<OwnershipTypeDTO> getOwnershipTypeByCountryId(long countryId){
        return ownershipTypeGraphRepository.findOwnershipTypeByCountry(countryId);
    }

    public OwnershipTypeDTO updateOwnershipType(long countryId, OwnershipTypeDTO ownershipTypeDTO){
        Boolean clinicTypeExistInCountryByName = ownershipTypeGraphRepository.ownershipTypeExistInCountryByName(countryId, "(?i)" + ownershipTypeDTO.getName(), ownershipTypeDTO.getId());
        if (clinicTypeExistInCountryByName) {
            exceptionService.duplicateDataException("error.OwnershipType.name.exist");
        }
        OwnershipType currentOwnershipType = ownershipTypeGraphRepository.findOne(ownershipTypeDTO.getId());
        if (currentOwnershipType!=null){
            currentOwnershipType.setName(ownershipTypeDTO.getName());
            currentOwnershipType.setDescription(ownershipTypeDTO.getDescription());
            ownershipTypeGraphRepository.save(currentOwnershipType);
        }
        return ownershipTypeDTO;
    }

    public boolean deleteOwnershipType(long ownershipTypeId){
        OwnershipType ownershipType = ownershipTypeGraphRepository.findOne(ownershipTypeId);
        if (ownershipType!=null){
            ownershipType.setEnabled(false);
            ownershipTypeGraphRepository.save(ownershipType);
        } else {
            exceptionService.dataNotFoundByIdException("error.OwnershipType.notfound");
        }
        return true;
    }
}
