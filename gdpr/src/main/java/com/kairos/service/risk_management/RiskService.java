package com.kairos.service.risk_management;


import com.kairos.dto.gdpr.BasicRiskDTO;
import com.kairos.dto.gdpr.data_inventory.OrganizationLevelRiskDTO;
import com.kairos.dto.user.organization.OrganizationResponseDTO;
import com.kairos.persistance.model.common.MongoBaseEntity;
import com.kairos.persistance.model.risk_management.Risk;
import com.kairos.persistance.repository.risk_management.RiskMongoRepository;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.commons.utils.ObjectMapperUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RiskService extends MongoBaseService {


    @Inject
    private RiskMongoRepository riskMongoRepository;


    @Inject
    private ExceptionService exceptionService;


    /**
     * @param <E>                  E here represent List of Risk DTO which extends Basic Risk DTO   {basic risk DTO is used at countryLevel and Organization Risk DTO used at organization level}
     * @param countryIdOrUnitId
     * @param risksRelatedToObject Map contain  Object of { Asset type,Asset Sub type, Processing Activity and Asset Object} as key and Risk Corresponding Risk dto list to them
     * @param <T>                  T { Asset type,Asset Sub type, Processing Activity and Asset Object}
     * @return method return  T { Asset type,Asset Sub type, Processing Activity and Asset Object} as key and List of Risk Ids generated after save operation
     */
    public <T extends MongoBaseEntity, E extends BasicRiskDTO> Map<T, List<BigInteger>> saveRiskAtCountryLevelOrOrganizationLevel(Long countryIdOrUnitId, boolean isUnitId, Map<T, List<E>> risksRelatedToObject) {

        Assert.notEmpty(risksRelatedToObject, "list can' t be empty");
        List<Risk> risks = new ArrayList<>();
        Map<T, List<Risk>> riskListRelatedToObjectMap = new HashMap<>();
        List<BigInteger> existingRiskIds = new ArrayList<>();
        Map<T, List<E>> existingRisksRelatedToObject = new HashMap<>();

        risksRelatedToObject.forEach((objectToWhichRiskRelated, riskDTOList) -> {
            List<E> existingRiskDTOS = new ArrayList<>();
            List<E> newRisk = new ArrayList<>();
            riskDTOList.forEach(riskDTO -> {
                if (Optional.ofNullable(riskDTO.getId()).isPresent()) {
                    existingRiskDTOS.add(riskDTO);
                    existingRiskIds.add(riskDTO.getId());
                } else {
                    newRisk.add(riskDTO);
                }
            });
            if (!existingRiskDTOS.isEmpty()) {
                existingRisksRelatedToObject.put(objectToWhichRiskRelated, existingRiskDTOS);

            }
            List<Risk> riskRelatedTOObject = new ArrayList<>();
            if (!newRisk.isEmpty()) {
                riskRelatedTOObject = isUnitId ? buildRiskAtOrganizationLevel(countryIdOrUnitId, newRisk) : buildRiskAtCountryLevel(countryIdOrUnitId, newRisk);
                risks.addAll(riskRelatedTOObject);
            }
            riskListRelatedToObjectMap.put(objectToWhichRiskRelated, riskRelatedTOObject);
        });
        if (!existingRisksRelatedToObject.isEmpty()) {
            risks.addAll(updateExistingRisk(countryIdOrUnitId, isUnitId, existingRiskIds, existingRisksRelatedToObject, riskListRelatedToObjectMap));
        }
        riskMongoRepository.saveAll(getNextSequence(risks));
        Map<T, List<BigInteger>> objectAndRiskIdsMap = new HashMap<>();
        riskListRelatedToObjectMap.forEach((objectToWhichRiskRelated, riskList) -> {
            List<BigInteger> riskIdList = new ArrayList<>();
            riskList.forEach(risk -> riskIdList.add(risk.getId()));
            objectAndRiskIdsMap.put(objectToWhichRiskRelated, riskIdList);
        });
        return objectAndRiskIdsMap;
    }


    private <T extends MongoBaseEntity, E extends BasicRiskDTO> List<Risk> updateExistingRisk(Long countryIdOrUnitId, boolean isUnitId,
                                                                                              List<BigInteger> existingRiskIds, Map<T, List<E>> existingRisksRelatedToObject, Map<T, List<Risk>> riskListRelatedToObjectMap) {
        Assert.notEmpty(existingRiskIds, "List can't be empty");
        List<Risk> riskList = isUnitId ? riskMongoRepository.findRiskByUnitIdAndIds(countryIdOrUnitId, existingRiskIds) : riskMongoRepository.findRiskByCountryIdAndIds(countryIdOrUnitId, existingRiskIds);
        Map<BigInteger, Risk> riskMap = riskList.stream().collect(Collectors.toMap(Risk::getId, risk -> risk));
        existingRisksRelatedToObject.forEach((objectToWhichRiskRelate, riskDTOS) ->
        {
            List<Risk> risksRelatesToObject = new ArrayList<>();
            riskDTOS.forEach(riskDTO -> {
                Risk risk = riskMap.get(riskDTO.getId());
                ObjectMapperUtils.copyPropertiesExceptSpecific(riskDTO, risk);
                risksRelatesToObject.add(risk);
            });
            riskListRelatedToObjectMap.get(objectToWhichRiskRelate).addAll(risksRelatesToObject);
        });
        return riskList;
    }


    /**
     * @param countryId countryId
     * @param riskDTOS  list of Risk Dto
     * @return
     */
    private <E extends BasicRiskDTO> List<Risk> buildRiskAtCountryLevel(Long countryId, List<E> riskDTOS) {

        checkForDuplicateNames(riskDTOS);
        List<Risk> riskList = new ArrayList<>();
        for (E riskDTO : riskDTOS) {
            Risk risk = new Risk(countryId, riskDTO.getName(), riskDTO.getDescription(),
                    riskDTO.getRiskRecommendation(), riskDTO.getRiskLevel());
            riskList.add(risk);
        }
        return riskList;

    }


    /**
     * @param unitId   - organizationId
     * @param riskDTOS list of Risk Dto
     * @return
     */
    private <E extends BasicRiskDTO> List<Risk> buildRiskAtOrganizationLevel(Long unitId, List<E> riskDTOS) {

        checkForDuplicateNames(riskDTOS);
        List<Risk> riskList = new ArrayList<>();
        for (E riskDTO : riskDTOS) {
            OrganizationLevelRiskDTO organizationLevelRiskDTO = (OrganizationLevelRiskDTO) riskDTO;
            Risk risk = new Risk(organizationLevelRiskDTO.getName(), organizationLevelRiskDTO.getDescription(),
                    organizationLevelRiskDTO.getRiskRecommendation(), organizationLevelRiskDTO.getRiskLevel(), organizationLevelRiskDTO.getDueDate());
            risk.setOrganizationId(unitId);
            risk.setRiskOwner(organizationLevelRiskDTO.getRiskOwner());
            riskList.add(risk);
        }
        return riskList;

    }


    private <E extends BasicRiskDTO> void checkForDuplicateNames(List<E> riskDTOS) {

        List<String> riskNames = new ArrayList<>();
        for (E riskDTO : riskDTOS) {
            if (riskNames.contains(riskDTO.getName().toLowerCase())) {
                exceptionService.duplicateDataException("message.duplicate", "Risk", riskDTO.getName());
            }
            riskNames.add(riskDTO.getName().toLowerCase());
        }
    }


}
