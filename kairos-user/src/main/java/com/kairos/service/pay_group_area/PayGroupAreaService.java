package com.kairos.service.pay_group_area;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.persistence.model.organization.Level;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.model.user.pay_group_area.MunicipalityPayGroupAreaWrapper;
import com.kairos.persistence.model.user.pay_group_area.PayGroupArea;
import com.kairos.persistence.model.user.pay_group_area.PayGroupAreaMunicipalityRelationship;
import com.kairos.persistence.model.user.region.Municipality;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.pay_level.PayGroupAreaGraphRepository;
import com.kairos.persistence.repository.user.region.MunicipalityGraphRepository;
import com.kairos.response.dto.web.pay_group_area.PayGroupAreaDTO;
import com.kairos.response.dto.web.pay_group_area.PayGroupAreaResponse;
import com.kairos.service.UserBaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    @Inject
    private Logger logger = LoggerFactory.getLogger(PayGroupArea.class);

    public PayGroupAreaDTO savePayGroupArea(Long countryId, PayGroupAreaDTO payGroupAreaDTO) {

        Country country = countryGraphRepository.findOne(countryId);
        if (!Optional.ofNullable(country).isPresent()) {
            throw new DataNotFoundByIdException("Invalid country id " + countryId);
        }
        Level level = countryGraphRepository.getLevel(countryId, payGroupAreaDTO.getLevelId());
        if (!Optional.ofNullable(level).isPresent()) {
            throw new DataNotFoundByIdException("Invalid level id " + payGroupAreaDTO.getLevelId());
        }
        Optional<Municipality> municipality = municipalityGraphRepository.findById(payGroupAreaDTO.getMunicipalityId());
        if (!Optional.ofNullable(municipality).isPresent()) {
            throw new DataNotFoundByIdException("Invalid municipality id " + payGroupAreaDTO.getMunicipalityId());
        }
        // Pay group area is already created Need to make a relationship with the new Municipality with pay group area
        if (Optional.ofNullable(payGroupAreaDTO.getId()).isPresent()) {
            PayGroupArea payGroupArea = payGroupAreaGraphRepository.findOne(payGroupAreaDTO.getId());
            if (!Optional.ofNullable(payGroupArea).isPresent() || payGroupArea.isDeleted() == true) {
                throw new DataNotFoundByIdException("Invalid pay group id");
            }

        } else {
            // creating a new Pay group area and creating relationship among them
            ObjectMapper objectMapper = new ObjectMapper();
            PayGroupArea payGroupArea = objectMapper.convertValue(payGroupAreaDTO, PayGroupArea.class);
            payGroupArea.setLevel(level);
            save(payGroupArea);
            PayGroupAreaMunicipalityRelationship municipalityRelationship
                    = new PayGroupAreaMunicipalityRelationship(payGroupArea, municipality.get(),
                    payGroupAreaDTO.getStartDateMillis().getTime(), payGroupAreaDTO.getEndDateMillis().getTime());
            save(municipalityRelationship);


        }

        return payGroupAreaDTO;
    }

//    public void validatePayGroupArea(List<MunicipalityPayGroupAreaWrapper> municipalitiesAndPayGroup, PayGroupAreaDTO payGroupAreaDTO) {
//
//        for (int i = 0; i < municipalitiesAndPayGroup.size(); i++) {
//            MunicipalityPayGroupAreaWrapper municipalityPayGroupAreaWrapper = municipalitiesAndPayGroup.get(i);
//            if (municipalityPayGroupAreaWrapper.getMunicipality() != null && payGroupAreaDTO.getMunicipalityId().contains(municipalityPayGroupAreaWrapper.getMunicipality().getId())) {
//
//            }
//        }
//    }

    public PayGroupAreaDTO updatePayGroupArea(Long payGroupAreaId, PayGroupAreaDTO payGroupAreaDTO) {
        PayGroupArea payGroupArea = getPayGroupAreaById(payGroupAreaId);
        payGroupArea.setName(payGroupArea.getName());
        save(payGroupArea);
        //  payGroupAreaDTO.setId(payGroupArea.getId());
        return payGroupAreaDTO;
    }

    public boolean deletePayGroupArea(Long payGroupAreaId) {
        PayGroupArea payGroupArea = payGroupAreaGraphRepository.findOne(payGroupAreaId);
        if (!Optional.ofNullable(payGroupArea).isPresent()) {
            logger.info("pay group area not found for deletion  ");
            throw new DataNotFoundByIdException("Invalid pay group id");
        }
        payGroupArea.setDeleted(true);
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

    public PayGroupAreaResponse getPayGroupArea(Long countryId) {

        Country country = countryGraphRepository.findOne(countryId, 0);
        if (country == null) {
            throw new InternalError("Invalid country id");
        }
        List<Level> levels = countryGraphRepository.getLevelsByCountry(countryId);
        List<Municipality> municipalities = municipalityGraphRepository.getMunicipalityByCountryId(countryId);
        PayGroupAreaResponse payGroupArea = new PayGroupAreaResponse(levels, municipalities, null);
        return payGroupArea;
    }
}
