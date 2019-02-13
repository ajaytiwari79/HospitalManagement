package com.kairos.service.country;

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
    @Inject
    private ExceptionService exceptionService;

    public KairosStatusDTO createKairosStatus(long countryId, KairosStatusDTO kairosStatusDTO){
        Country country = countryGraphRepository.findOne(countryId);
        KairosStatus kairosStatus = null;
        if (country == null) {
            exceptionService.dataNotFoundByIdException("message.country.id.notFound", countryId);
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
        return kairosStatusGraphRepository.findKairosStatusByCountry(countryId);
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
}
