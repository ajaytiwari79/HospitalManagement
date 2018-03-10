package com.kairos.service.pay_level;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DataNotMatchedException;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.model.user.pay_level.PayGroupArea;
import com.kairos.persistence.model.user.region.Municipality;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.pay_level.PayGroupAreaGraphRepository;
import com.kairos.persistence.repository.user.region.MunicipalityGraphRepository;
import com.kairos.response.dto.web.pay_level.PayGroupAreaDTO;
import com.kairos.service.UserBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Created by prabjot on 21/12/17.
 */
@Transactional
@Service
public class PayGroupAreaService extends UserBaseService {

    @Inject
    private PayGroupAreaGraphRepository payGroupAreaGraphRepository;
    @Inject
    private CountryGraphRepository countryGraphRepository;
    @Inject
    private MunicipalityGraphRepository municipalityGraphRepository;

    public PayGroupAreaDTO savePayGroupArea(Long countryId, PayGroupAreaDTO payGroupAreaDTO) {

        Country country = countryGraphRepository.findOne(countryId);
        if (!Optional.ofNullable(country).isPresent()) {
            throw new DataNotFoundByIdException("Invalid country id " + countryId);
        }
        Set<Municipality> municipalities = municipalityGraphRepository.getMunicipalitiesByIds(payGroupAreaDTO.getMunicipalityId());
        if (municipalities.size() != payGroupAreaDTO.getMunicipalityId().size()) {
            throw new DataNotMatchedException("Incorrect muncipality List " + countryId);
        }
        payGroupAreaDTO.setId(null);
        ObjectMapper objectMapper = new ObjectMapper();
        PayGroupArea payGroupArea = objectMapper.convertValue(payGroupAreaDTO, PayGroupArea.class);
        payGroupArea.setMunicipality(municipalities);
        save(payGroupArea);

        payGroupAreaDTO.setId(payGroupArea.getId());

        return payGroupAreaDTO;
    }

    public PayGroupAreaDTO updatePayGroupArea(Long payGroupAreaId, PayGroupAreaDTO payGroupAreaDTO) {
        PayGroupArea payGroupArea = getPayGroupAreaById(payGroupAreaId);
        payGroupArea.setName(payGroupArea.getName());
        save(payGroupArea);
        //  payGroupAreaDTO.setId(payGroupArea.getId());
        return payGroupAreaDTO;
    }

    public boolean deletePayGroupArea(Long payGroupAreaId) {
        PayGroupArea payGroupArea = getPayGroupAreaById(payGroupAreaId);
        save(payGroupArea);
        return true;
    }

    private PayGroupArea getPayGroupAreaById(Long payGroupAreaId) {
        PayGroupArea payGroupArea = payGroupAreaGraphRepository.findOne(payGroupAreaId);
        if (payGroupArea == null) {
            throw new DataNotFoundByIdException("Invalid pay group id");
        }
        return payGroupArea;
    }

    public List<PayGroupArea> getPayGroupArea(Long countryId) {
        Country country = countryGraphRepository.findOne(countryId, 0);
        if (country == null) {
            throw new InternalError("Invalid country id");
        }
        return new ArrayList<>();
    }
}
