package com.kairos.service.pay_group_area;

import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.user.country.pay_group_area.PayGroupAreaDTO;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.pay_group_area.PayGroupAreaResponse;
import com.kairos.persistence.model.organization.Level;
import com.kairos.persistence.model.user.pay_group_area.PayGroupArea;
import com.kairos.persistence.model.user.pay_group_area.PayGroupAreaMunicipalityRelationship;
import com.kairos.persistence.model.user.pay_group_area.PayGroupAreaQueryResult;
import com.kairos.persistence.model.user.region.Municipality;
import com.kairos.persistence.repository.user.country.CountryGraphRepository;
import com.kairos.persistence.repository.user.pay_group_area.PayGroupAreaGraphRepository;
import com.kairos.persistence.repository.user.pay_group_area.PayGroupAreaRelationshipRepository;
import com.kairos.persistence.repository.user.region.MunicipalityGraphRepository;
import com.kairos.service.exception.ExceptionService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.asLocalDate;
import static com.kairos.commons.utils.ObjectUtils.distinctByKey;


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
    private final Logger LOGGER = LoggerFactory.getLogger(PayGroupArea.class);


    public List<PayGroupAreaQueryResult> savePayGroupArea(Long countryId, List<PayGroupAreaDTO> payGroupAreaDTOS) {
        payGroupAreaDTOS = payGroupAreaDTOS.stream().filter(distinctByKey(PayGroupAreaDTO::getMunicipalityId)).collect(Collectors.toList());
        PayGroupAreaDTO payGroupAreaDTO = payGroupAreaDTOS.get(0);
        List<Long> municipalityIds = payGroupAreaDTOS.stream().map(PayGroupAreaDTO::getMunicipalityId).collect(Collectors.toList());
        // PatGroup Area id not present so checking name in level
        if (!Optional.ofNullable(payGroupAreaDTO.getPayGroupAreaId()).isPresent() && payGroupAreaGraphRepository.isPayGroupAreaExistWithNameInLevel(payGroupAreaDTO.getLevelId(), payGroupAreaDTO.getName())) {
            exceptionService.duplicateDataException("message.payGroupArea.exists", payGroupAreaDTO.getName());
        }
        Level level = countryGraphRepository.getLevel(countryId, payGroupAreaDTO.getLevelId());
        if (!Optional.ofNullable(level).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.country.level.id.notFound", payGroupAreaDTO.getLevelId());

        }
        List<Municipality> municipalities = municipalityGraphRepository.findAllById(municipalityIds);
        if (municipalities.size() != municipalityIds.size()) {
            exceptionService.dataNotFoundByIdException("message.paygroup.all_municipality.notFound");
        }
        Map<Long, Municipality> municipalityMap = municipalities.stream().collect(Collectors.toMap(Municipality::getId, Function.identity()));
        // Pay group area is already created Need to make a relationship with the new Municipality with pay group area
        List<PayGroupAreaQueryResult> payGroupAreas = payGroupAreaGraphRepository
                .findPayGroupAreaByLevelAndMunicipality(payGroupAreaDTO.getLevelId(), municipalityIds, -1L);

        validateAllPayGroupAreaByLevelAndMunicipality(new ArrayList<>(payGroupAreaDTOS), payGroupAreas);
        PayGroupArea payGroupArea;
        if (Optional.ofNullable(payGroupAreaDTO.getPayGroupAreaId()).isPresent()) {
            payGroupArea = payGroupAreaGraphRepository.findOne(payGroupAreaDTO.getPayGroupAreaId());
            if (!Optional.ofNullable(payGroupArea).isPresent() || payGroupArea.isDeleted()) {
                exceptionService.dataNotFoundByIdException("message.paygroup.id.notfound", payGroupAreaDTO.getPayGroupAreaId());

            }
        } else {
            // creating a new Pay group area and creating relationship among them

            payGroupArea = new PayGroupArea(payGroupAreaDTO.getName(), payGroupAreaDTO.getDescription(), level);
            payGroupAreaGraphRepository.save(payGroupArea);
            LOGGER.info("pay group area Id {}",payGroupArea.getId());
        }

        List<PayGroupAreaMunicipalityRelationship> municipalityRelationships = new ArrayList<>();
        payGroupAreaDTOS.forEach(payGroupAreaDTO1 -> {
            Long endDateMillis = (payGroupAreaDTO1.getEndDateMillis() != null) ? payGroupAreaDTO1.getEndDateMillis().getTime() : null;
            municipalityRelationships.add(new PayGroupAreaMunicipalityRelationship(payGroupArea, municipalityMap.get(payGroupAreaDTO1.getMunicipalityId()),
                    payGroupAreaDTO1.getStartDateMillis().getTime(), endDateMillis));
        });

        payGroupAreaRelationshipRepository.saveAll(municipalityRelationships);
        return municipalityRelationships.stream().map(a->new PayGroupAreaQueryResult(payGroupArea, a, a.getMunicipality())).collect(Collectors.toList());
    }

    public PayGroupAreaQueryResult updatePayGroupArea(Long payGroupAreaId, PayGroupAreaDTO payGroupAreaDTO) {
        Optional<PayGroupAreaMunicipalityRelationship> municipalityRelationship = payGroupAreaRelationshipRepository.findById(payGroupAreaDTO.getId());
        if (!municipalityRelationship.isPresent()) {
            LOGGER.info("pay group area not found");
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
                .findPayGroupAreaByLevelAndMunicipality(payGroupAreaDTO.getLevelId(), Collections.singletonList(payGroupAreaDTO.getMunicipalityId()), payGroupAreaDTO.getId());

        validateAllPayGroupAreaByLevelAndMunicipality(Collections.singletonList(payGroupAreaDTO), payGroupAreas);


        Long endDateMillis = (payGroupAreaDTO.getEndDateMillis() != null) ? payGroupAreaDTO.getEndDateMillis().getTime() : null;

        PayGroupAreaQueryResult payGroupAreaQueryResult;
        if (!payGroupAreaDTO.getMunicipalityId().equals(municipalityRelationship.get().getMunicipality().getId())) {
            // user has changed the municipality we need to
            LOGGER.info(payGroupAreaDTO.getMunicipalityId() + "-----CHANGED-----" + (municipalityRelationship.get().getMunicipality().getId()));
            Optional<Municipality> municipality = municipalityGraphRepository.findById(payGroupAreaDTO.getMunicipalityId());
            if (!municipality.isPresent()) {
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

    private void validateAllPayGroupAreaByLevelAndMunicipality(List<PayGroupAreaDTO> payGroupAreaDTOs, List<PayGroupAreaQueryResult> payGroupAreas) {
        for (PayGroupAreaDTO payGroupAreaDTO : payGroupAreaDTOs) {
            for (PayGroupAreaQueryResult payGroupArea : payGroupAreas) {
                if (payGroupAreaDTO.getEndDateMillis() != null) {
                    if (payGroupArea.getEndDateMillis() != null) {
                        if (new Date(payGroupArea.getStartDateMillis()).before(payGroupAreaDTO.getEndDateMillis())
                                && new Date(payGroupArea.getEndDateMillis()).after(payGroupAreaDTO.getStartDateMillis())) {
                            exceptionService.actionNotPermittedException("message.paygroup.daterange.overlap1", asLocalDate(payGroupArea.getStartDateMillis()), asLocalDate(payGroupAreaDTO.getEndDateMillis()), asLocalDate(payGroupArea.getEndDateMillis()), asLocalDate(payGroupAreaDTO.getStartDateMillis()));
                        }
                    } else {
                        if (payGroupAreaDTO.getEndDateMillis().after(new Date(payGroupArea.getStartDateMillis()))) {
                            Long dateOneDayLessStartDate = payGroupAreaDTO.getStartDateMillis().getTime() - (24 * 60 * 60 * 1000);
                            payGroupAreaGraphRepository.updateEndDateOfPayGroupArea(payGroupArea.getId(), payGroupArea.getPayGroupAreaId(), payGroupAreaDTO.getMunicipalityId(), dateOneDayLessStartDate);
                        } else {
                            exceptionService.actionNotPermittedException("message.paygroup.daterange.overlap", asLocalDate(payGroupAreaDTO.getEndDateMillis()), asLocalDate(payGroupArea.getStartDateMillis()));
                        }
                    }
                } else {
                    if (payGroupArea.getEndDateMillis() != null && new Date(payGroupArea.getEndDateMillis()).after(payGroupAreaDTO.getStartDateMillis())) {
                        exceptionService.actionNotPermittedException("message.paygroup.daterange.overlapold", payGroupAreaDTO.getStartDateMillis(), (new Date(payGroupArea.getEndDateMillis())));
                    } else {
                        LOGGER.info(payGroupAreaDTO.getStartDateMillis() + "to create CURRENT -->" + (new Date(payGroupArea.getStartDateMillis())));
                        if (payGroupAreaDTO.getStartDateMillis().after(new Date(payGroupArea.getStartDateMillis()))) {
                            Long dateOneDayLessStartDate = DateUtils.minusDays(payGroupAreaDTO.getStartDateMillis(),1).getTime();
                            LOGGER.info(new Date(dateOneDayLessStartDate) + " new Date to update--------------");
                            payGroupAreaGraphRepository.updateEndDateOfPayGroupArea(payGroupArea.getId(), payGroupArea.getPayGroupAreaId(), payGroupAreaDTO.getMunicipalityId(), dateOneDayLessStartDate);
                        } else {
                            exceptionService.actionNotPermittedException("message.paygroup.daterange.overlap",asLocalDate(payGroupAreaDTO.getStartDateMillis()), asLocalDate(payGroupArea.getStartDateMillis()));

                        }
                    }
                }
            }
        }
    }

    public boolean deletePayGroupArea(Long payGroupAreaId) {
        PayGroupArea payGroupArea = payGroupAreaGraphRepository.findOne(payGroupAreaId);
        if (!Optional.ofNullable(payGroupArea).isPresent() || payGroupArea.isDeleted()) {
            LOGGER.info("pay group area not found for deletion  ");
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
        return payGroupAreaGraphRepository.getPayGroupAreaWithMunicipalityByOrganizationLevelId(levelId);
    }

    public PayGroupAreaResponse getMunicipalityAndOrganizationLevel(Long countryId) {
        Country country = countryGraphRepository.findOne(countryId, 0);
        if (!Optional.ofNullable(country).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.country.id.notFound", countryId);

        }
        List<Level> organizationLevels = countryGraphRepository.getLevelsByCountry(countryId);
        List<Municipality> municipalities = municipalityGraphRepository.getMunicipalityByCountryId(countryId);
        return new PayGroupAreaResponse(organizationLevels, municipalities);
    }


    public boolean deletePayGroupFromMunicipality(Long payGroupAreaId, Long municipalityId, Long relationshipId) {
        PayGroupArea payGroupArea = payGroupAreaGraphRepository.findOne(payGroupAreaId);
        if (!Optional.ofNullable(payGroupArea).isPresent() || payGroupArea.isDeleted()) {
            LOGGER.info("pay group area not found for deletion  ");
            exceptionService.dataNotFoundByIdException("message.paygroup.id.notfound", payGroupAreaId);

        }

        int linkedMunicipalityCount = payGroupAreaGraphRepository.removePayGroupAreaFromMunicipality(payGroupAreaId, municipalityId, relationshipId);
        if (linkedMunicipalityCount == 0) {
            payGroupArea.setDeleted(true);
            payGroupAreaGraphRepository.save(payGroupArea);
        }
        return true;
    }

    public List<PayGroupAreaQueryResult> getPayGroupAreaByLevel(Long levelId) {
        return payGroupAreaGraphRepository.getPayGroupAreaByOrganizationLevelId(levelId);
    }
}
