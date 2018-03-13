package com.kairos.service.pay_group_area;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.custom_exception.ActionNotPermittedException;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.persistence.model.organization.Level;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.model.user.pay_group_area.PayGroupArea;
import com.kairos.persistence.model.user.pay_group_area.PayGroupAreaMunicipalityRelationship;
import com.kairos.persistence.model.user.pay_group_area.PayGroupAreaQueryResult;
import com.kairos.persistence.model.user.region.Municipality;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.pay_group_area.PayGroupAreaRelationshipRepository;
import com.kairos.persistence.repository.user.pay_level.PayGroupAreaGraphRepository;
import com.kairos.persistence.repository.user.region.MunicipalityGraphRepository;
import com.kairos.response.dto.web.pay_group_area.PayGroupAreaDTO;
import com.kairos.response.dto.web.pay_group_area.PayGroupAreaResponse;
import com.kairos.service.UserBaseService;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
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
    private PayGroupAreaRelationshipRepository payGroupAreaRelationshipRepository;

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
        getAllPayGroupAreaByLevelAndMunicipality(payGroupAreaDTO);
        if (Optional.ofNullable(payGroupAreaDTO.getId()).isPresent()) {
            PayGroupArea payGroupArea = payGroupAreaGraphRepository.findOne(payGroupAreaDTO.getId());
            if (!Optional.ofNullable(payGroupArea).isPresent() || payGroupArea.isDeleted() == true) {
                throw new DataNotFoundByIdException("Invalid pay group id");
            }

        } else {
            // creating a new Pay group area and creating relationship among them
            ObjectMapper objectMapper = new ObjectMapper();
            PayGroupArea payGroupArea = new PayGroupArea();
            payGroupArea.setId(null);
            payGroupArea = objectMapper.convertValue(payGroupAreaDTO, PayGroupArea.class);
            payGroupArea.setLevel(level);

            payGroupAreaGraphRepository.save(payGroupArea);
            logger.info(payGroupArea.getId().toString());
            PayGroupAreaMunicipalityRelationship municipalityRelationship
                    = new PayGroupAreaMunicipalityRelationship(payGroupArea, municipality.get(),
                    payGroupAreaDTO.getStartDateMillis().getTime(), payGroupAreaDTO.getEndDateMillis().getTime());
            //  payGroupAreaRelationshipRepository.save(municipalityRelationship);


        }

        return payGroupAreaDTO;
    }

    public PayGroupAreaDTO updatePayGroupArea(Long payGroupAreaId, PayGroupAreaDTO payGroupAreaDTO) {
        PayGroupArea payGroupArea = getPayGroupAreaById(payGroupAreaId);
        payGroupArea.setName(payGroupArea.getName());
        save(payGroupArea);
        //  payGroupAreaDTO.setId(payGroupArea.getId());
        return payGroupAreaDTO;
    }

    private void getAllPayGroupAreaByLevelAndMunicipality(PayGroupAreaDTO payGroupAreaDTO) {
        List<PayGroupAreaQueryResult> payGroupAreas = payGroupAreaGraphRepository.findPayGroupAreaByLevelAndMunicipality(payGroupAreaDTO.getLevelId(), payGroupAreaDTO.getMunicipalityId());
        for (int i = 0; i < payGroupAreas.size(); i++) {
            if (payGroupAreaDTO.getEndDateMillis() != null) {
                if (payGroupAreas.get(i).getEndDateMillis() != null) {
                    if (new DateTime(payGroupAreas.get(i).getStartDateMillis()).isAfter(new DateTime(payGroupAreaDTO.getEndDateMillis()))
                            || new DateTime(payGroupAreas.get(i).getEndDateMillis()).isBefore(new DateTime(payGroupAreaDTO.getStartDateMillis()))) {
                        continue;
                    } else {
                        throw new ActionNotPermittedException("Overlap date range" + new DateTime(payGroupAreas.get(i).getStartDateMillis())
                                + " " + (new DateTime(payGroupAreaDTO.getEndDateMillis())) + " " + new DateTime(payGroupAreas.get(i).getEndDateMillis()) + " " + (new DateTime(payGroupAreaDTO.getStartDateMillis())));
                    }
                } else {
                    //current db object have null end date.
                    logger.info(" CASE 1: current object have both end date and start date");
                    if (new DateTime(payGroupAreaDTO.getEndDateMillis()).isBefore(new DateTime(payGroupAreas.get(i).getStartDateMillis()))) {
                        //going to update the end date in db
                        Long dateOneDayLessStartDate = payGroupAreaDTO.getStartDateMillis().getTime() - (24 * 60 * 60 * 1000);
                        payGroupAreaGraphRepository.updateEndDateOfPayGroupArea(payGroupAreas.get(i).getId(), payGroupAreaDTO.getMunicipalityId(), dateOneDayLessStartDate);
                    }
                    else {
                        logger.info("e");
                        throw new ActionNotPermittedException("Overlap date range"+new DateTime(payGroupAreaDTO.getEndDateMillis())+" "+(new DateTime(payGroupAreas.get(i).getStartDateMillis())));
                    }

                }
            } else {
                if (payGroupAreas.get(i).getEndDateMillis() != null) {
                    logger.info(" CASE 3,2,4: current object have both end date and start date");
                } else {
                    logger.info(" CASE 1,2,6: current object have both end date and start date");
                }
            }
        }

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
        List<Level> organizationLevels = countryGraphRepository.getLevelsByCountry(countryId);
        List<Municipality> municipalities = municipalityGraphRepository.getMunicipalityByCountryId(countryId);
        List<PayGroupAreaQueryResult> payGroupAreas = payGroupAreaGraphRepository.getPayGroupAreaByCountry(countryId);
        PayGroupAreaResponse payGroupArea = new PayGroupAreaResponse(organizationLevels, municipalities, payGroupAreas);
        return payGroupArea;
    }
}
