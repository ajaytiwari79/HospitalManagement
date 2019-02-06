package com.kairos.service.country;

import com.kairos.persistence.model.country.default_data.IndustryTypeDTO;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.default_data.IndustryType;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.IndustryTypeGraphRepository;
import com.kairos.service.exception.ExceptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by oodles on 9/1/17.
 */
@Service
@Transactional
public class IndustryTypeService {
    @Inject
    private IndustryTypeGraphRepository industryTypeGraphRepository;
    @Inject
    private CountryGraphRepository countryGraphRepository;
    @Inject
    private ExceptionService exceptionService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public IndustryTypeDTO createIndustryType(long countryId, IndustryTypeDTO industryTypeDTO){
        Country country = countryGraphRepository.findOne(countryId);
        IndustryType industryType = null;
        if ( country == null) {
            exceptionService.dataNotFoundByIdException("message.country.id.notFound", countryId);
        } else {
            Boolean industryTypeExistInCountryByName = industryTypeGraphRepository.industryTypeExistInCountryByName(countryId, "(?i)" + industryTypeDTO.getName(), -1L);
            if (industryTypeExistInCountryByName) {
                exceptionService.duplicateDataException("error.IndustryType.name.exist");
            }
            industryType = new IndustryType(industryTypeDTO.getName(),industryTypeDTO.getDescription());
            industryType.setCountry(country);
            industryTypeGraphRepository.save(industryType);
        }
        industryTypeDTO.setId(industryType.getId());
        return industryTypeDTO;
    }

    public List<IndustryTypeDTO> getIndustryTypeByCountryId(long countryId){
        return industryTypeGraphRepository.findIndustryTypeByCountry(countryId);
    }

    public IndustryTypeDTO updateIndustryType(long countryId, IndustryTypeDTO industryTypeDTO){
        Boolean industryTypeExistInCountryByName = industryTypeGraphRepository.industryTypeExistInCountryByName(countryId, "(?i)" + industryTypeDTO.getName(), industryTypeDTO.getId());
        if (industryTypeExistInCountryByName) {
            exceptionService.duplicateDataException("error.IndustryType.name.exist");
        }
        IndustryType currentIndustryType = industryTypeGraphRepository.findOne(industryTypeDTO.getId());
        if (currentIndustryType != null){
            currentIndustryType.setName(industryTypeDTO.getName());
            currentIndustryType.setDescription(industryTypeDTO.getDescription());
            industryTypeGraphRepository.save(currentIndustryType);
        }
        return industryTypeDTO;
    }

    public boolean deleteIndustryType(long industryTypeId){
        IndustryType currentIndustryType = industryTypeGraphRepository.findOne(industryTypeId);
        if (currentIndustryType!=null){
            currentIndustryType.setEnabled(false);
            industryTypeGraphRepository.save(currentIndustryType);
        } else {
            exceptionService.dataNotFoundByIdException("error.IndustryType.notfound");
        }
        return true;
    }
}
