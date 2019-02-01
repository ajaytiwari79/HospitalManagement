package com.kairos.service.risk_management;


import com.kairos.dto.gdpr.BasicRiskDTO;
import com.kairos.dto.gdpr.data_inventory.OrganizationLevelRiskDTO;
import com.kairos.persistence.model.risk_management.Risk;
import com.kairos.persistence.repository.risk_management.RiskDaoImpl;
import com.kairos.persistence.repository.risk_management.RiskRepository;
import com.kairos.response.dto.common.RiskResponseDTO;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

@Service
public class RiskService{

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private RiskRepository riskRepository;

    @Inject
    private RiskDaoImpl riskDaoImpl;

    /**
     * @param <E>                  E here represent List of RISK DTO which extends Basic RISK DTO   {basic risk DTO is used at countryLevel and Organization RISK DTO used at organization level}
     * @param referenceId
     * @param risksRelatedToObject Map contain  Object of { Asset type,Asset Sub type, Processing Activity and Asset Object} as key and RISK Corresponding RISK dto list to them
     * @param <T>                  T { Asset type,Asset Sub type, Processing Activity and Asset Object}
     * @return method return  T { Asset type,Asset Sub type, Processing Activity and Asset Object} as key and List of RISK Ids generated after save operation
     */
    public <T, E extends BasicRiskDTO> Map<T, List<Risk>> saveRiskAtCountryLevelOrOrganizationLevel(Long referenceId, boolean isUnitId, Map<T, List<E>> risksRelatedToObject) {

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
                    //existingRiskIds.add(riskDTO.getId());
                } else {
                    newRisk.add(riskDTO);
                }
            });
            if (!existingRiskDTOS.isEmpty()) {
                existingRisksRelatedToObject.put(objectToWhichRiskRelated, existingRiskDTOS);

            }
            List<Risk> riskRelatedTOObject = new ArrayList<>();
            if (!newRisk.isEmpty()) {
                riskRelatedTOObject = isUnitId ? buildRiskAtOrganizationLevel(referenceId, newRisk) : buildRiskAtCountryLevel(referenceId, newRisk);
                risks.addAll(riskRelatedTOObject);
            }
            riskListRelatedToObjectMap.put(objectToWhichRiskRelated, riskRelatedTOObject);
        });
        if (!existingRisksRelatedToObject.isEmpty()) {
            risks.addAll(updateExistingRisk(referenceId, isUnitId, existingRiskIds, existingRisksRelatedToObject, riskListRelatedToObjectMap));
        }
       // if (CollectionUtils.isNotEmpty(risks)) riskMongoRepository.saveAll(getNextSequence(risks));
        return riskListRelatedToObjectMap;
    }


    private <T, E extends BasicRiskDTO> List<Risk> updateExistingRisk(Long referenceId, boolean isUnitId,
                                                                                              List<BigInteger> existingRiskIds, Map<T, List<E>> existingRisksRelatedToObject, Map<T, List<Risk>> riskListRelatedToObjectMap) {
        Assert.notEmpty(existingRiskIds, "List can't be empty");
       // List<Risk> riskList = isUnitId ? riskMongoRepository.findRiskByUnitIdAndIds(referenceId, existingRiskIds) : riskMongoRepository.findRiskByCountryIdAndIds(referenceId, existingRiskIds);
        //Map<BigInteger, Risk> riskMap = riskList.stream().collect(Collectors.toMap(Risk::getId, risk -> risk));
        existingRisksRelatedToObject.forEach((objectToWhichRiskRelate, riskDTOS) ->
        {
            List<Risk> risksRelatesToObject = new ArrayList<>();
            riskDTOS.forEach(riskDTO -> {
                //Risk risk = riskMap.get(riskDTO.getId());
                //ObjectMapperUtils.copyProperties(riskDTO, risk);
                //risksRelatesToObject.add(risk);
            });
            riskListRelatedToObjectMap.get(objectToWhichRiskRelate).addAll(risksRelatesToObject);
        });
        return new ArrayList<>();
    }


    /**
     * @param countryId countryId
     * @param riskDTOS  list of RISK Dto
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
     * @param riskDTOS list of RISK Dto
     * @return
     */
    private <E extends BasicRiskDTO> List<Risk> buildRiskAtOrganizationLevel(Long unitId, List<E> riskDTOS) {

        checkForDuplicateNames(riskDTOS);
        List<Risk> riskList = new ArrayList<>();
        for (E riskDTO : riskDTOS) {
            OrganizationLevelRiskDTO organizationLevelRiskDTO = (OrganizationLevelRiskDTO) riskDTO;
            Risk risk = new Risk(organizationLevelRiskDTO.getName(), organizationLevelRiskDTO.getDescription(),
                    organizationLevelRiskDTO.getRiskRecommendation(), organizationLevelRiskDTO.getRiskLevel());
            risk.setReminderActive(organizationLevelRiskDTO.isReminderActive());
            risk.setDaysToReminderBefore(organizationLevelRiskDTO.getDaysToReminderBefore());
           // risk.setOrganizationId(unitId);
            riskList.add(risk);
        }
        return riskList;

    }


    private <E extends BasicRiskDTO> void checkForDuplicateNames(List<E> riskDTOS) {

        List<String> riskNames = new ArrayList<>();
        for (E riskDTO : riskDTOS) {
            if (riskNames.contains(riskDTO.getName().toLowerCase())) {
                exceptionService.duplicateDataException("message.duplicate", "RISK", riskDTO.getName());
            }
            riskNames.add(riskDTO.getName().toLowerCase());
        }
    }


    public List<RiskResponseDTO> getAllRiskByUnitId(Long unitId) {
        return riskDaoImpl.getAllRiskOfOrganizationId(unitId);
    }


}
