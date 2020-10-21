package com.kairos.service.pay_group_area;

import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.utils.CommonsExceptionUtil;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.TranslationUtil;
import com.kairos.dto.TranslationInfo;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.distinctByKey;
import static com.kairos.constants.UserMessagesConstants.*;


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

    public List<PayGroupAreaQueryResult> savePayGroupArea(Long countryId, List<PayGroupAreaDTO> payGroupAreaDTOS) {
        payGroupAreaDTOS = payGroupAreaDTOS.stream().filter(distinctByKey(PayGroupAreaDTO::getMunicipalityId)).collect(Collectors.toList());
        PayGroupAreaDTO payGroupAreaDTO = payGroupAreaDTOS.get(0);
        List<Long> municipalityIds = payGroupAreaDTOS.stream().map(PayGroupAreaDTO::getMunicipalityId).collect(Collectors.toList());
        // PatGroup Area id not present so checking name in level
        Level level = countryGraphRepository.getLevel(countryId, payGroupAreaDTO.getLevelId());
        List<Municipality> municipalities = municipalityGraphRepository.findAllById(municipalityIds);
        validateDetails(payGroupAreaDTO, municipalityIds, level, municipalities);
        Map<Long, Municipality> municipalityMap = municipalities.stream().collect(Collectors.toMap(Municipality::getId, Function.identity()));
        // Pay group area is already created Need to make a relationship with the new Municipality with pay group area
        List<PayGroupAreaQueryResult> payGroupAreas = payGroupAreaGraphRepository.findPayGroupAreaByLevelAndMunicipality(payGroupAreaDTO.getLevelId(), municipalityIds, -1L);
        validateAllPayGroupAreaByLevelAndMunicipality(new ArrayList<>(payGroupAreaDTOS), payGroupAreas);
        PayGroupArea payGroupArea;
        if (Optional.ofNullable(payGroupAreaDTO.getPayGroupAreaId()).isPresent()) {
            payGroupArea = payGroupAreaGraphRepository.findOne(payGroupAreaDTO.getPayGroupAreaId());
            if (!Optional.ofNullable(payGroupArea).isPresent() || payGroupArea.isDeleted()) {
                exceptionService.dataNotFoundByIdException(MESSAGE_PAYGROUP_ID_NOTFOUND, payGroupAreaDTO.getPayGroupAreaId());
            }
        } else {
            payGroupArea = new PayGroupArea(payGroupAreaDTO.getName(), payGroupAreaDTO.getDescription(), level);
            payGroupAreaGraphRepository.save(payGroupArea);
        }
        List<PayGroupAreaMunicipalityRelationship> municipalityRelationships = new ArrayList<>();
        payGroupAreaDTOS.forEach(payGroupAreaDTO1 -> {
            Long endDateMillis = (payGroupAreaDTO1.getEndDateMillis() != null) ? payGroupAreaDTO1.getEndDateMillis().getTime() : null;
            municipalityRelationships.add(new PayGroupAreaMunicipalityRelationship(payGroupArea, municipalityMap.get(payGroupAreaDTO1.getMunicipalityId()), payGroupAreaDTO1.getStartDateMillis().getTime(), endDateMillis));
        });
        payGroupAreaRelationshipRepository.saveAll(municipalityRelationships);
        return municipalityRelationships.stream().map(a -> new PayGroupAreaQueryResult(payGroupArea, a, a.getMunicipality())).collect(Collectors.toList());
    }

    private void validateDetails(PayGroupAreaDTO payGroupAreaDTO, List<Long> municipalityIds, Level level, List<Municipality> municipalities) {
        if (!Optional.ofNullable(payGroupAreaDTO.getPayGroupAreaId()).isPresent() && payGroupAreaGraphRepository.isPayGroupAreaExistWithNameInLevel(payGroupAreaDTO.getLevelId(), payGroupAreaDTO.getName())) {
            exceptionService.duplicateDataException(MESSAGE_PAYGROUPAREA_EXISTS, payGroupAreaDTO.getName());
        }

        if (!Optional.ofNullable(level).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_COUNTRY_LEVEL_ID_NOTFOUND, payGroupAreaDTO.getLevelId());

        }

        if (municipalities.size() != municipalityIds.size()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_PAYGROUP_ALL_MUNICIPALITY_NOTFOUND);
        }
    }

    public PayGroupAreaQueryResult updatePayGroupArea(Long payGroupAreaId, PayGroupAreaDTO payGroupAreaDTO) {
        PayGroupAreaMunicipalityRelationship municipalityRelationship = payGroupAreaRelationshipRepository.findById(payGroupAreaDTO.getId()).orElseThrow(()->new DataNotFoundByIdException(CommonsExceptionUtil.convertMessage(MESSAGE_PAYGROUP_ID_NOTFOUND, payGroupAreaDTO.getId())));
        // PayGroup Area name duplicacy
        if (!municipalityRelationship.getPayGroupArea().getName().equals(payGroupAreaDTO.getName().trim())) {
            boolean existAlready = payGroupAreaGraphRepository.isPayGroupAreaExistWithNameInLevel(payGroupAreaDTO.getLevelId(), payGroupAreaDTO.getName().trim(), payGroupAreaId);
            if (existAlready) {
                exceptionService.duplicateDataException(MESSAGE_PAYGROUPAREA_EXISTS, payGroupAreaDTO.getName());
            }
        }
        if(!payGroupAreaDTO.getStartDateMillis().equals(DateUtils.getDate(municipalityRelationship.getStartDateMillis()))){
            exceptionService.actionNotPermittedException(MESSAGE_PAYGROUPAREA_GAP_NOT_CREATE);
        }

        List<PayGroupAreaQueryResult> payGroupAreas = payGroupAreaGraphRepository.findPayGroupAreaByLevelAndMunicipality(payGroupAreaDTO.getLevelId(), Collections.singletonList(payGroupAreaDTO.getMunicipalityId()), payGroupAreaDTO.getId());
        validateAllPayGroupAreaByLevelAndMunicipality(Collections.singletonList(payGroupAreaDTO), payGroupAreas);
        Long endDateMillis = (payGroupAreaDTO.getEndDateMillis() != null) ? payGroupAreaDTO.getEndDateMillis().getTime() : null;

        PayGroupAreaQueryResult payGroupAreaQueryResult;
        payGroupAreaQueryResult = savePayGroupAreaRelationShip(payGroupAreaId, payGroupAreaDTO, municipalityRelationship, endDateMillis);
        return payGroupAreaQueryResult;
    }

    private PayGroupAreaQueryResult savePayGroupAreaRelationShip(Long payGroupAreaId, PayGroupAreaDTO payGroupAreaDTO, PayGroupAreaMunicipalityRelationship municipalityRelationship, Long endDateMillis) {
        PayGroupAreaQueryResult payGroupAreaQueryResult;
        if (!payGroupAreaDTO.getMunicipalityId().equals(municipalityRelationship.getMunicipality().getId())) {
            // user has changed the municipality we need to
            Municipality municipality = municipalityGraphRepository.findById(payGroupAreaDTO.getMunicipalityId()).orElseThrow(()->new DataNotFoundByIdException(CommonsExceptionUtil.convertMessage(MESSAGE_PAYGROUP_MUNICIPALITY_NOTFOUND, payGroupAreaDTO.getMunicipalityId())));
            payGroupAreaGraphRepository.removePayGroupAreaFromMunicipality(payGroupAreaId, municipalityRelationship.getMunicipality().getId(), payGroupAreaDTO.getId());

            PayGroupAreaMunicipalityRelationship municipalityNewRelation = new PayGroupAreaMunicipalityRelationship(municipalityRelationship.getPayGroupArea(), municipality, payGroupAreaDTO.getStartDateMillis().getTime(), endDateMillis);
            municipalityNewRelation.getPayGroupArea().setName(payGroupAreaDTO.getName().trim());
            municipalityNewRelation.getPayGroupArea().setDescription(payGroupAreaDTO.getDescription());
            payGroupAreaRelationshipRepository.save(municipalityNewRelation);
            payGroupAreaQueryResult = new PayGroupAreaQueryResult(municipalityNewRelation.getPayGroupArea(), municipalityNewRelation, municipality);
        } else {
            municipalityRelationship.setEndDateMillis(endDateMillis);
            municipalityRelationship.setStartDateMillis(payGroupAreaDTO.getStartDateMillis().getTime());
            municipalityRelationship.getPayGroupArea().setName(payGroupAreaDTO.getName().trim());
            municipalityRelationship.getPayGroupArea().setDescription(payGroupAreaDTO.getDescription());
            payGroupAreaRelationshipRepository.save(municipalityRelationship);
            payGroupAreaQueryResult = new PayGroupAreaQueryResult(municipalityRelationship.getPayGroupArea(), municipalityRelationship, municipalityRelationship.getMunicipality());
        }
        return payGroupAreaQueryResult;
    }

    private void validateAllPayGroupAreaByLevelAndMunicipality(List<PayGroupAreaDTO> payGroupAreaDTOs, List<PayGroupAreaQueryResult> payGroupAreas) {
        for (PayGroupAreaDTO payGroupAreaDTO : payGroupAreaDTOs) {
            for (int i = 0; i < payGroupAreas.size(); i++) {
                if (payGroupAreaDTO.getEndDateMillis() != null) {
                    validateDates(payGroupAreas, payGroupAreaDTO, i);
                } else {
                    if (payGroupAreas.get(i).getEndDateMillis() != null) {
                        validateDate(payGroupAreas, payGroupAreaDTO, i);
                    } else {
                        updateEndDateInPayGroupArea(payGroupAreaDTO.getStartDateMillis(),payGroupAreas, payGroupAreaDTO, i);
                    }
                }
            }
        }
    }

    private void validateDate(List<PayGroupAreaQueryResult> payGroupAreas, PayGroupAreaDTO payGroupAreaDTO, int i) {
        if (new DateTime(payGroupAreas.get(i).getEndDateMillis()).isAfter(new DateTime(payGroupAreaDTO.getStartDateMillis()))) {
            exceptionService.actionNotPermittedException(MESSAGE_PAYGROUP_DATERANGE_OVERLAPOLD, new DateTime(payGroupAreaDTO.getStartDateMillis()), (new DateTime(payGroupAreas.get(i).getEndDateMillis())));
        }
    }

    private void validateDates(List<PayGroupAreaQueryResult> payGroupAreas, PayGroupAreaDTO payGroupAreaDTO, int i) {
        if (payGroupAreas.get(i).getEndDateMillis() != null) {
            if (new DateTime(payGroupAreas.get(i).getStartDateMillis()).isBefore(new DateTime(payGroupAreaDTO.getEndDateMillis()))
                    && new DateTime(payGroupAreas.get(i).getEndDateMillis()).isAfter(new DateTime(payGroupAreaDTO.getStartDateMillis()))) {
                exceptionService.actionNotPermittedException(MESSAGE_PAYGROUP_DATERANGE_OVERLAP1, new DateTime(payGroupAreas.get(i).getStartDateMillis()), (new DateTime(payGroupAreaDTO.getEndDateMillis())), new DateTime(payGroupAreas.get(i).getEndDateMillis()), (new DateTime(payGroupAreaDTO.getStartDateMillis())));
            }
        } else {
            updateEndDateInPayGroupArea(payGroupAreaDTO.getEndDateMillis(),payGroupAreas, payGroupAreaDTO, i);
        }
    }

    private void updateEndDateInPayGroupArea(Date dateInMillis, List<PayGroupAreaQueryResult> payGroupAreas, PayGroupAreaDTO payGroupAreaDTO, int i) {
        if (new DateTime(dateInMillis).isAfter(new DateTime(payGroupAreas.get(i).getStartDateMillis()))) {
            Long dateOneDayLessStartDate = payGroupAreaDTO.getStartDateMillis().getTime() - (24 * 60 * 60 * 1000);
            payGroupAreaGraphRepository.updateEndDateOfPayGroupArea(payGroupAreas.get(i).getId(), payGroupAreas.get(i).getPayGroupAreaId(), payGroupAreaDTO.getMunicipalityId(), dateOneDayLessStartDate);
        } else {
            exceptionService.actionNotPermittedException(MESSAGE_PAYGROUP_DATERANGE_OVERLAP, new DateTime(payGroupAreaDTO.getEndDateMillis()), (new DateTime(payGroupAreas.get(i).getStartDateMillis())));

        }
    }

    public boolean deletePayGroupArea(Long payGroupAreaId) {
        PayGroupArea payGroupArea = payGroupAreaGraphRepository.findOne(payGroupAreaId);
        if (!Optional.ofNullable(payGroupArea).isPresent() || payGroupArea.isDeleted()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_PAYGROUP_ID_NOTFOUND, payGroupAreaId);
        }
        if (payGroupAreaGraphRepository.isLinkedWithPayTable(payGroupAreaId)) {
            exceptionService.dataNotFoundByIdException(MESSAGE_PAYGROUP_USED);
        }
        if(payGroupAreaGraphRepository.isLinkedWithPayTable(payGroupAreaId)){
            exceptionService.dataNotFoundByIdException(MESSAGE_PAYGROUP_USED);
        }

        payGroupArea.setDeleted(true);
        payGroupAreaGraphRepository.save(payGroupArea);
        return true;
    }

    public List<PayGroupAreaQueryResult> getPayGroupArea(Long countryId, Long levelId) {
        Level level = countryGraphRepository.getLevel(countryId, levelId);
        if (!Optional.ofNullable(level).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_PAYGROUP_LEVEL_NOTFOUND);

        }
        List<PayGroupAreaQueryResult> payGroupAreaQueryResults = payGroupAreaGraphRepository.getPayGroupAreaWithMunicipalityByOrganizationLevelId(levelId);
        payGroupAreaQueryResults.forEach(payGroupAreaQueryResult -> {
            payGroupAreaQueryResult.setCountryId(countryId);
            payGroupAreaQueryResult.setTranslations(TranslationUtil.getTranslatedData(payGroupAreaQueryResult.getTranslatedNames(),payGroupAreaQueryResult.getTranslatedDescriptions()));
        });
        return payGroupAreaQueryResults;
    }

    public PayGroupAreaResponse getMunicipalityAndOrganizationLevel(Long countryId) {
        Country country = countryGraphRepository.findOne(countryId, 0);
        if (!Optional.ofNullable(country).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_COUNTRY_ID_NOTFOUND, countryId);

        }
        List<Level> organizationLevels = countryGraphRepository.getLevelsByCountry(countryId);
        List<Municipality> municipalities = municipalityGraphRepository.getMunicipalityByCountryId(countryId);
        return new PayGroupAreaResponse(organizationLevels, municipalities);
    }


    public boolean deletePayGroupFromMunicipality(Long payGroupAreaId, Long municipalityId, Long relationshipId) {
        PayGroupArea payGroupArea = payGroupAreaGraphRepository.findOne(payGroupAreaId);
        if (!Optional.ofNullable(payGroupArea).isPresent() || payGroupArea.isDeleted()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_PAYGROUP_ID_NOTFOUND, payGroupAreaId);
        }

        if (payGroupAreaGraphRepository.isLinkedWithPayTable(payGroupAreaId)) {
            exceptionService.dataNotFoundByIdException(MESSAGE_PAYGROUP_USED);
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

    public Map<String, TranslationInfo> updateTranslationOfPayGroupArea(Long payGroupAreaId, Map<String,TranslationInfo> translations) {
        Map<String,String> translatedNames = new HashMap<>();
        Map<String,String> translatedDescriptios = new HashMap<>();
        for(Map.Entry<String,TranslationInfo> entry :translations.entrySet()){
            translatedNames.put(entry.getKey(),entry.getValue().getName());
            translatedDescriptios.put(entry.getKey(),entry.getValue().getDescription());
        }
        PayGroupArea payGroupArea =payGroupAreaGraphRepository.findOne(payGroupAreaId);
        payGroupArea.setTranslatedNames(translatedNames);
        payGroupArea.setTranslatedDescriptions(translatedDescriptios);
        payGroupAreaGraphRepository.save(payGroupArea);
        return payGroupArea.getTranslatedData();
    }
}
