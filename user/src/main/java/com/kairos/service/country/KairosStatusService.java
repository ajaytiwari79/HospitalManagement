package com.kairos.service.country;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.commons.utils.TranslationUtil;
import com.kairos.dto.TranslationInfo;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.default_data.BusinessType;
import com.kairos.persistence.model.country.default_data.KairosStatus;
import com.kairos.persistence.model.country.default_data.KairosStatusDTO;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.country.KairosStatusGraphRepository;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kairos.constants.UserMessagesConstants.MESSAGE_COUNTRY_ID_NOTFOUND;

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
    @Inject
    private ExceptionService exceptionService;

    public KairosStatusDTO createKairosStatus(long countryId, KairosStatusDTO kairosStatusDTO){
        Country country = countryGraphRepository.findOne(countryId);
        KairosStatus kairosStatus = null;
        if (country == null) {
            exceptionService.dataNotFoundByIdException(MESSAGE_COUNTRY_ID_NOTFOUND, countryId);
        } else {
            Boolean kairosStatusExistInCountryByName = kairosStatusGraphRepository.kairosStatusExistInCountryByName(countryId, "(?i)" + kairosStatusDTO.getName(), -1L);
            if (kairosStatusExistInCountryByName) {
                exceptionService.duplicateDataException("error.BusinessType.name.exist");
            }
            kairosStatus = new KairosStatus(kairosStatusDTO.getName(), kairosStatusDTO.getDescription());
            kairosStatus.setCountry(country);
            kairosStatusGraphRepository.save(kairosStatus);
        }
        kairosStatusDTO.setId(kairosStatus.getId());
        return kairosStatusDTO;
    }

    public List<KairosStatusDTO> getKairosStatusByCountryId(long countryId){
        List<KairosStatus> kairosStatusList = kairosStatusGraphRepository.findKairosStatusByCountry(countryId);
        List<KairosStatusDTO> kairosStatusDTOS = ObjectMapperUtils.copyCollectionPropertiesByMapper(kairosStatusList,KairosStatusDTO.class);
        for(KairosStatusDTO kairosStatusDTO:kairosStatusDTOS){
            kairosStatusDTO.setCountryId(countryId);
            kairosStatusDTO.setTranslations(TranslationUtil.getTranslatedData(kairosStatusDTO.getTranslatedNames(),kairosStatusDTO.getTranslatedDescriptions()));
        }
        return kairosStatusDTOS;
    }

    public KairosStatusDTO updateKairosStatus(long countryId, KairosStatusDTO kairosStatusDTO){
        Boolean kairosStatusExistInCountryByName = kairosStatusGraphRepository.kairosStatusExistInCountryByName(countryId, "(?i)" + kairosStatusDTO.getName(), kairosStatusDTO.getId());
        if (kairosStatusExistInCountryByName) {
            exceptionService.duplicateDataException("error.KairosStatus.name.exist");
        }
        KairosStatus currentKairosStatus = kairosStatusGraphRepository.findOne(kairosStatusDTO.getId());
        if (currentKairosStatus != null) {
            currentKairosStatus.setName(kairosStatusDTO.getName());
            currentKairosStatus.setDescription(kairosStatusDTO.getDescription());
            kairosStatusGraphRepository.save(currentKairosStatus);
        }
        return kairosStatusDTO;
    }

    public boolean deleteKairosStatus(long kairosStatusId){
        KairosStatus kairosStatus = kairosStatusGraphRepository.findOne(kairosStatusId);
        if (kairosStatus !=null){
            kairosStatus.setEnabled(false);
            kairosStatusGraphRepository.save(kairosStatus);
        } else {
            exceptionService.dataNotFoundByIdException("error.KairosStatus.notfound");
        }
        return true;
    }

    public Map<String, TranslationInfo> updateTranslation(Long kairosStatusId, Map<String,TranslationInfo> translations) {
        Map<String,String> translatedNames = new HashMap<>();
        Map<String,String> translatedDescriptions = new HashMap<>();
        TranslationUtil.updateTranslationData(translations,translatedNames,translatedDescriptions);
        KairosStatus kairosStatus =kairosStatusGraphRepository.findOne(kairosStatusId);
        kairosStatus.setTranslatedNames(translatedNames);
        kairosStatus.setTranslatedDescriptions(translatedDescriptions);
        kairosStatusGraphRepository.save(kairosStatus);
        return kairosStatus.getTranslatedData();
    }
}
