package com.kairos.service.country;

import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.default_data.CitizenStatus;
import com.kairos.persistence.model.country.default_data.CitizenStatusDTO;
import com.kairos.persistence.repository.user.country.CitizenStatusGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.FormatUtil;
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
    @Inject
    private ExceptionService exceptionService;

    public CitizenStatusDTO createCitizenStatus(long countryId, CitizenStatusDTO citizenStatusDTO){
        Country country = countryGraphRepository.findOne(countryId);
        CitizenStatus citizenStatus = null;
        if (country == null) {
            exceptionService.dataNotFoundByIdException("message.country.id.notFound", countryId);
        } else {
            Boolean citizenStatusExistInCountryByName = citizenStatusGraphRepository.citizenStatusExistInCountryByName(countryId, "(?i)" + citizenStatusDTO.getName(), -1L);
            if (citizenStatusExistInCountryByName) {
                exceptionService.duplicateDataException("error.CitizenStatus.name.exist");
            }
            citizenStatus = new CitizenStatus(citizenStatusDTO.getName(), citizenStatusDTO.getDescription());
            citizenStatus.setCountry(country);
            citizenStatusGraphRepository.save(citizenStatus);
        }
        citizenStatusDTO.setId(citizenStatus.getId());
        return citizenStatusDTO;
    }

    public CitizenStatusDTO updateCitizenStatus(CitizenStatusDTO citizenStatusDTO, long countryId){
        Boolean citizenStatusExistInCountryByName = citizenStatusGraphRepository.citizenStatusExistInCountryByName(countryId, "(?i)" + citizenStatusDTO.getName(), citizenStatusDTO.getId());
        if (citizenStatusExistInCountryByName) {
            exceptionService.duplicateDataException("error.CitizenStatus.name.exist");
        }
        CitizenStatus currentCitizenStatus = citizenStatusGraphRepository.findOne(citizenStatusDTO.getId());
        if (currentCitizenStatus != null) {
            currentCitizenStatus.setName(citizenStatusDTO.getName());
            currentCitizenStatus.setDescription(citizenStatusDTO.getDescription());
            citizenStatusGraphRepository.save(currentCitizenStatus);
        }
        return citizenStatusDTO;
    }


    public boolean deleteCitizenStatus(long citizenStatusId){
        CitizenStatus currentCivilianStatus = citizenStatusGraphRepository.findOne(citizenStatusId);
        if (currentCivilianStatus!=null){
            currentCivilianStatus.setEnabled(false);
            citizenStatusGraphRepository.save(currentCivilianStatus);
        } else {
            exceptionService.dataNotFoundByIdException("error.CitizenStatus.notfound");
        }
        return true;
    }

    public List<CitizenStatusDTO> getCitizenStatusByCountryId(long countryId){
        return citizenStatusGraphRepository.findCitizenStatusByCountryId(countryId);
    }


    public List<Map<String,Object>> getCitizenStatusByCountryIdAnotherFormat(long countryId){
        List<Map<String, Object>> data = citizenStatusGraphRepository.findCitizenStatusByCountryIdAnotherFormat(countryId);
        if(data==null){
          return  null;
        }
        return FormatUtil.formatNeoResponse(data);
    }


}
