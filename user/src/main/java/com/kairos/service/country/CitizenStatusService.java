package com.kairos.service.country;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.commons.utils.TranslationUtil;
import com.kairos.dto.TranslationInfo;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.default_data.CitizenStatus;
import com.kairos.persistence.model.country.default_data.CitizenStatusDTO;
import com.kairos.persistence.model.country.default_data.Currency;
import com.kairos.persistence.repository.user.country.CitizenStatusGraphRepository;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.FormatUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kairos.constants.UserMessagesConstants.MESSAGE_COUNTRY_ID_NOTFOUND;

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
            exceptionService.dataNotFoundByIdException(MESSAGE_COUNTRY_ID_NOTFOUND, countryId);
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
        List<CitizenStatus> citizenStatusList = citizenStatusGraphRepository.findCitizenStatusByCountryId(countryId);
        List<CitizenStatusDTO> citizenStatusDTOS = ObjectMapperUtils.copyCollectionPropertiesByMapper(citizenStatusList,CitizenStatusDTO.class);
        citizenStatusDTOS.forEach(citizenStatusDTO -> {
            citizenStatusDTO.setCountryId(countryId);
            citizenStatusDTO.setTranslations(TranslationUtil.getTranslatedData(citizenStatusDTO.getTranslatedNames(),citizenStatusDTO.getTranslatedDescriptions()));
        });
        return citizenStatusDTOS;
    }


    public List<Map<String,Object>> getCitizenStatusByCountryIdAnotherFormat(long countryId){
        List<Map<String, Object>> data = citizenStatusGraphRepository.findCitizenStatusByCountryIdAnotherFormat(countryId);
        if(data==null){
          return  null;
        }
        return FormatUtil.formatNeoResponse(data);
    }

    public Map<String, TranslationInfo> updateTranslation(Long citizenStatusId, Map<String,TranslationInfo> translations) {
        Map<String,String> translatedNames = new HashMap<>();
        Map<String,String> translatedDescriptions = new HashMap<>();
        TranslationUtil.updateTranslationData(translations,translatedNames,translatedDescriptions);
        CitizenStatus citizenStatus =citizenStatusGraphRepository.findOne(citizenStatusId);
        citizenStatus.setTranslatedNames(translatedNames);
        citizenStatus.setTranslatedDescriptions(translatedDescriptions);
        citizenStatusGraphRepository.save(citizenStatus);
        return citizenStatus.getTranslatedData();
    }


}
