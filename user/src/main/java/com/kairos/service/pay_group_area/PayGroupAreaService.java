package com.kairos.service.pay_group_area;

import com.kairos.persistence.model.organization.Level;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.user.pay_group_area.PayGroupArea;
import com.kairos.persistence.model.user.pay_group_area.PayGroupAreaMunicipalityRelationship;
import com.kairos.persistence.model.user.pay_group_area.PayGroupAreaQueryResult;
import com.kairos.persistence.model.user.region.Municipality;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.pay_group_area.PayGroupAreaRelationshipRepository;
import com.kairos.persistence.repository.user.pay_group_area.PayGroupAreaGraphRepository;
import com.kairos.persistence.repository.user.region.MunicipalityGraphRepository;
import com.kairos.dto.user.country.pay_group_area.PayGroupAreaDTO;
import com.kairos.persistence.model.country.pay_group_area.PayGroupAreaResponse;
import com.kairos.service.exception.ExceptionService;
import org.joda.time.DateTime;
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
public class PayGroupAreaService {

    @Inject
    private PayGroupAreaGraphRepository payGroupAreaGraphRepository;
    @Inject
    private CountryGraphRepository countryGraphRepository;
    @Inject
    private MunicipalityGraphRepository municipalityGraphRepository;
    @Inject
    private PayGroupAreaRelationshipRepository payGroupAreaRelationshipRepository;
    @Inject
    private ExceptionService exceptionService;
    private Logger logger = LoggerFactory.getLogger(PayGroupArea.class);

    public PayGroupAreaQueryResult savePayGroupArea(Long countryId, PayGroupAreaDTO payGroupAreaDTO) {

        // PatGroup Area id not present so checking name in level
        if (!Optional.ofNullable(payGroupAreaDTO.getPayGroupAreaId()).isPresent() && payGroupAreaGraphRepository.isPayGroupAreaExistWithNameInLevel(payGroupAreaDTO.getLevelId(), payGroupAreaDTO.getName().trim())) {
            exceptionService.duplicateDataException("message.payGroupArea.exists", payGroupAreaDTO.getName());
        }
        Level level = countryGraphRepository.getLevel(countryId, payGroupAreaDTO.getLevelId());
        if (!Optional.ofNullable(level).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.country.level.id.notFound", payGroupAreaDTO.getLevelId());

        }
        Optional<Municipality> municipality = municipalityGraphRepository.findById(payGroupAreaDTO.getMunicipalityId());
        if (!Optional.ofNullable(municipality).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.paygroup.municipality.notFound", payGroupAreaDTO.getMunicipalityId());

        }
        // Pay group area is already created Need to make a relationship with the new Municipality with pay group area
        List<PayGroupAreaQueryResult> payGroupAreas = payGroupAreaGraphRepository
                .findPayGroupAreaByLevelAndMunicipality(payGroupAreaDTO.getLevelId(), payGroupAreaDTO.getMunicipalityId(), -1L);

        validateAllPayGroupAreaByLevelAndMunicipality(payGroupAreaDTO, payGroupAreas);
        PayGroupArea payGroupArea;
        if (Optional.ofNullable(payGroupAreaDTO.getPayGroupAreaId()).isPresent()) {
            payGroupArea = payGroupAreaGraphRepository.findOne(payGroupAreaDTO.getPayGroupAreaId());
            if (!Optional.ofNullable(payGroupArea).isPresent() || payGroupArea.isDeleted()) {
                exceptionService.dataNotFoundByIdException("message.paygroup.id.notfound", payGroupAreaDTO.getPayGroupAreaId());

            }
        } else {
            // creating a new Pay group area and creating relationship among them

            payGroupArea = new PayGroupArea(payGroupAreaDTO.getName().trim(), payGroupAreaDTO.getDescription(), level);
            payGroupAreaGraphRepository.save(payGroupArea);
            logger.info(payGroupArea.getId().toString());
        }
        Long endDateMillis = (payGroupAreaDTO.getEndDateMillis() != null) ? payGroupAreaDTO.getEndDateMillis().getTime() : null;
        PayGroupAreaMunicipalityRelationship municipalityRelationship = new PayGroupAreaMunicipalityRelationship(payGroupArea, municipality.get(),
                payGroupAreaDTO.getStartDateMillis().getTime(), endDateMillis);
        payGroupAreaRelationshipRepository.save(municipalityRelationship);
        PayGroupAreaQueryResult payGroupAreaQueryResult = new PayGroupAreaQueryResult(payGroupArea, municipalityRelationship, municipality.get());
        payGroupAreaQueryResult.setId(municipalityRelationship.getId());
        return payGroupAreaQueryResult;
    }

    public PayGroupAreaQueryResult updatePayGroupArea(Long payGroupAreaId, PayGroupAreaDTO payGroupAreaDTO) {
        Optional<PayGroupAreaMunicipalityRelationship> municipalityRelationship = payGroupAreaRelationshipRepository.findById(payGroupAreaDTO.getId());
        if (!municipalityRelationship.isPresent()) {
            logger.info("pay group area not found");
            exceptionService.dataNotFoundByIdException("message.paygroup.id.notfound", payGroupAreaDTO.getId());
        }
        // PayGroup Area name duplicacy
        if (!municipalityRelationship.get().getPayGroupArea().getName().equals(payGroupAreaDTO.getName().trim())) {
            boolean existAlready = payGroupAreaGraphRepository.isPayGroupAreaExistWithNameInLevel(payGroupAreaDTO.getLevelId(), payGroupAreaDTO.getName().trim(), payGroupAreaId);
            if (existAlready) {
                exceptionService.duplicateDataException("message.payGroupArea.exists", payGroupAreaDTO.getName());
            }
        }

        List<PayGroupAreaQueryResult> payGroupAreas = payGroupAreaGraphRepository
                .findPayGroupAreaByLevelAndMunicipality(payGroupAreaDTO.getLevelId(), payGroupAreaDTO.getMunicipalityId(), payGroupAreaDTO.getId());

        validateAllPayGroupAreaByLevelAndMunicipality(payGroupAreaDTO, payGroupAreas);


        Long endDateMillis = (payGroupAreaDTO.getEndDateMillis() != null) ? payGroupAreaDTO.getEndDateMillis().getTime() : null;

        PayGroupAreaQueryResult payGroupAreaQueryResult;
        if (!payGroupAreaDTO.getMunicipalityId().equals(municipalityRelationship.get().getMunicipality().getId())) {
            // user has changed the municipality we need to
            logger.info(payGroupAreaDTO.getMunicipalityId() + "-----CHANGED-----" + (municipalityRelationship.get().getMunicipality().getId()));
            Optional<Municipality> municipality = municipalityGraphRepository.findById(payGroupAreaDTO.getMunicipalityId());
            if (!Optional.ofNullable(municipality).isPresent()) {
                exceptionService.dataNotFoundByIdException("message.paygroup.municipality.notFound", payGroupAreaDTO.getMunicipalityId());

            }

            payGroupAreaGraphRepository.removePayGroupAreaFromMunicipality(payGroupAreaId, municipalityRelationship.get().getMunicipality().getId(), payGroupAreaDTO.getId());

            PayGroupAreaMunicipalityRelationship municipalityNewRelation = new PayGroupAreaMunicipalityRelationship(municipalityRelationship.get().getPayGroupArea(), municipality.get(),
                    payGroupAreaDTO.getStartDateMillis().getTime(), endDateMillis);
            municipalityNewRelation.getPayGroupArea().setName(payGroupAreaDTO.getName().trim());
            municipalityNewRelation.getPayGroupArea().setDescription(payGroupAreaDTO.getDescription());
            payGroupAreaRelationshipRepository.save(municipalityNewRelation);
            payGroupAreaQueryResult = new PayGroupAreaQueryResult(municipalityNewRelation.getPayGroupArea(), municipalityNewRelation, municipality.get());

        } else {
            municipalityRelationship.get().setEndDateMillis(endDateMillis);
            municipalityRelationship.get().setStartDateMillis(payGroupAreaDTO.getStartDateMillis().getTime());
            municipalityRelationship.get().getPayGroupArea().setName(payGroupAreaDTO.getName().trim());
            municipalityRelationship.get().getPayGroupArea().setDescription(payGroupAreaDTO.getDescription());
            payGroupAreaRelationshipRepository.save(municipalityRelationship.get());
            payGroupAreaQueryResult = new PayGroupAreaQueryResult(municipalityRelationship.get().getPayGroupArea(), municipalityRelationship.get(), municipalityRelationship.get().getMunicipality());
            // User hasnt changes the municipacity only related data needs to be changed
        }
        return payGroupAreaQueryResult;
    }

    private void validateAllPayGroupAreaByLevelAndMunicipality(PayGroupAreaDTO payGroupAreaDTO, List<PayGroupAreaQueryResult> payGroupAreas) {

        for (int i = 0; i < payGroupAreas.size(); i++) {
            if (payGroupAreaDTO.getEndDateMillis() != null) {
                if (payGroupAreas.get(i).getEndDateMillis() != null) {
                    if (new DateTime(payGroupAreas.get(i).getStartDateMillis()).isBefore(new DateTime(payGroupAreaDTO.getEndDateMillis()))
                            && new DateTime(payGroupAreas.get(i).getEndDateMillis()).isAfter(new DateTime(payGroupAreaDTO.getStartDateMillis()))) {
                        exceptionService.actionNotPermittedException("message.paygroup.daterange.overlap1", new DateTime(payGroupAreas.get(i).getStartDateMillis()), (new DateTime(payGroupAreaDTO.getEndDateMillis())), new DateTime(payGroupAreas.get(i).getEndDateMillis()), (new DateTime(payGroupAreaDTO.getStartDateMillis())));
                        //throw new ActionNotPermittedException("Overlap date range" + new DateTime(payGroupAreas.get(i).getStartDate())
                        //        + " " + (new DateTime(payGroupAreaDTO.getEndDate())) + " " + new DateTime(payGroupAreas.get(i).getEndDate()) + " " + (new DateTime(payGroupAreaDTO.getStartDate())));
                    }
                } else {
                    if (new DateTime(payGroupAreaDTO.getEndDateMillis()).isAfter(new DateTime(payGroupAreas.get(i).getStartDateMillis()))) {
                        Long dateOneDayLessStartDate = payGroupAreaDTO.getStartDateMillis().getTime() - (24 * 60 * 60 * 1000);
                        payGroupAreaGraphRepository.updateEndDateOfPayGroupArea(payGroupAreas.get(i).getId(), payGroupAreas.get(i).getPayGroupAreaId(), payGroupAreaDTO.getMunicipalityId(), dateOneDayLessStartDate);
                    } else {
                        exceptionService.actionNotPermittedException("message.paygroup.daterange.overlap", new DateTime(payGroupAreaDTO.getEndDateMillis()), (new DateTime(payGroupAreas.get(i).getStartDateMillis())));

                    }
                }
            } else {
                if (payGroupAreas.get(i).getEndDateMillis() != null) {
                    if (new DateTime(payGroupAreas.get(i).getEndDateMillis()).isAfter(new DateTime(payGroupAreaDTO.getStartDateMillis()))) {
                        exceptionService.actionNotPermittedException("message.paygroup.daterange.overlapold", new DateTime(payGroupAreaDTO.getStartDateMillis()), (new DateTime(payGroupAreas.get(i).getEndDateMillis())));

                    }
                } else {
                    logger.info(new DateTime(payGroupAreaDTO.getStartDateMillis()) + "to create CURRENT -->" + (new DateTime(payGroupAreas.get(i).getStartDateMillis())));
                    if (new DateTime(payGroupAreaDTO.getStartDateMillis()).isAfter(new DateTime(payGroupAreas.get(i).getStartDateMillis()))) {
                        Long dateOneDayLessStartDate = payGroupAreaDTO.getStartDateMillis().getTime() - (24 * 60 * 60 * 1000);
                        logger.info(new DateTime(dateOneDayLessStartDate) + " new Date to update--------------");
                        payGroupAreaGraphRepository.updateEndDateOfPayGroupArea(payGroupAreas.get(i).getId(), payGroupAreas.get(i).getPayGroupAreaId(), payGroupAreaDTO.getMunicipalityId(), dateOneDayLessStartDate);
                    } else {
                        exceptionService.actionNotPermittedException("message.paygroup.daterange.overlap", new DateTime(payGroupAreaDTO.getStartDateMillis()), (new DateTime(payGroupAreas.get(i).getStartDateMillis())));

                    }
                }
            }
        }
    }

    public boolean deletePayGroupArea(Long payGroupAreaId) {
        PayGroupArea payGroupArea = payGroupAreaGraphRepository.findOne(payGroupAreaId);
        if (!Optional.ofNullable(payGroupArea).isPresent() || payGroupArea.isDeleted()) {
            logger.info("pay group area not found for deletion  ");
            exceptionService.dataNotFoundByIdException("message.paygroup.id.notfound", payGroupAreaId);

        }
        payGroupArea.setDeleted(true);
        payGroupAreaGraphRepository.save(payGroupArea);
        return true;
    }

    public List<PayGroupAreaQueryResult> getPayGroupArea(Long countryId, Long levelId) {
        Level level = countryGraphRepository.getLevel(countryId, levelId);
        if (!Optional.ofNullable(level).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.paygroup.level.notfound");

        }
        List<PayGroupAreaQueryResult> payGroupAreas = payGroupAreaGraphRepository.getPayGroupAreaWithMunicipalityByOrganizationLevelId(levelId);
        return payGroupAreas;
    }

    public PayGroupAreaResponse getMunicipalityAndOrganizationLevel(Long countryId) {
        Country country = countryGraphRepository.findOne(countryId, 0);
        if (!Optional.ofNullable(country).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.country.id.notFound", countryId);

        }
        List<Level> organizationLevels = countryGraphRepository.getLevelsByCountry(countryId);
        List<Municipality> municipalities = municipalityGraphRepository.getMunicipalityByCountryId(countryId);
        PayGroupAreaResponse payGroupArea = new PayGroupAreaResponse(organizationLevels, municipalities);
        return payGroupArea;
    }


    public boolean deletePayGroupFromMunicipality(Long payGroupAreaId, Long municipalityId, Long relationshipId) {
        PayGroupArea payGroupArea = payGroupAreaGraphRepository.findOne(payGroupAreaId);
        if (!Optional.ofNullable(payGroupArea).isPresent() || payGroupArea.isDeleted()) {
            logger.info("pay group area not found for deletion  ");
            exceptionService.dataNotFoundByIdException("message.paygroup.id.notfound", payGroupAreaId);

        }

      int linkedMunicipalityCount=  payGroupAreaGraphRepository.removePayGroupAreaFromMunicipality(payGroupAreaId, municipalityId, relationshipId);
        if (linkedMunicipalityCount==0){
            payGroupArea.setDeleted(true);
            payGroupAreaGraphRepository.save(payGroupArea);
        }
        return true;
    }

}
