package com.kairos.service.risk_management;


import com.kairos.gdpr.data_inventory.RiskDTO;
import com.kairos.persistance.model.common.MongoBaseEntity;
import com.kairos.persistance.model.risk_management.Risk;
import com.kairos.persistance.repository.risk_management.RiskMongoRepository;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.util.ObjectMapperUtils;
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
     * @param countryId
     * @param risksRelatedToObject Map contain  Object of { Asset type,Asset Sub type, Processing Activity and Asset Object} as key and Risk Coressponding Risk dto list to them
     * @param <T>                  T { Asset type,Asset Sub type, Processing Activity and Asset Object}
     * @return method return  T { Asset type,Asset Sub type, Processing Activity and Asset Object} as key and List of Risk Ids generated after save operation
     */
    public <T extends MongoBaseEntity> Map<T, List<BigInteger>> saveRiskAtCountryLevel(Long countryId, Map<T, List<RiskDTO>> risksRelatedToObject) {

        Assert.notEmpty(risksRelatedToObject, "list can' t be empty");
        List<Risk> risks = new ArrayList<>();
        Map<T, List<Risk>> riskListRelatedToObjectMap = new HashMap<>();
        List<BigInteger> existingRiskIds = new ArrayList<>();
        Map<T, List<RiskDTO>> existingRisksRelatedToObject = new HashMap<>();

        risksRelatedToObject.forEach((objectToWhichRiskRelated, riskDTOList) -> {
            List<RiskDTO> existingRiskDTOS = new ArrayList<>();
            List<RiskDTO> newRisk = new ArrayList<>();
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
            List<Risk> riskRelatedTOObject = buildRiskAtCountryLevel(countryId, newRisk);
            risks.addAll(riskRelatedTOObject);
            riskListRelatedToObjectMap.put(objectToWhichRiskRelated, riskRelatedTOObject);
        });
        if (!existingRisksRelatedToObject.isEmpty()) {
            risks.addAll(updateExisitingRisk(countryId, existingRiskIds, existingRisksRelatedToObject, riskListRelatedToObjectMap));
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


    private <T extends MongoBaseEntity> List<Risk> updateExisitingRisk(Long countryId, List<BigInteger> riskIds, Map<T, List<RiskDTO>> existingRisksRelatedToObject, Map<T, List<Risk>> riskListRelatedToObjectMap) {
        Assert.notEmpty(riskIds, "List can't be empty");
        List<Risk> riskList = riskMongoRepository.findRiskByCountryIdAndIds(countryId, riskIds);
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
    private List<Risk> buildRiskAtCountryLevel(Long countryId, List<RiskDTO> riskDTOS) {

        checkForDuplicateNames(riskDTOS);
        List<Risk> riskList = new ArrayList<>();
        for (RiskDTO riskDTO : riskDTOS) {
            Risk risk = new Risk(countryId, riskDTO.getName(), riskDTO.getDescription(),
                    riskDTO.getRiskRecommendation(), riskDTO.getRiskLevel());
            riskList.add(risk);
        }
        return riskList;

    }

    private void checkForDuplicateNames(List<RiskDTO> riskDTOS) {

        List<String> riskNames = new ArrayList<>();
        for (RiskDTO riskDTO : riskDTOS) {
            if (riskNames.contains(riskDTO.getName().toLowerCase())) {
                exceptionService.duplicateDataException("message.duplicate", "Risk", riskDTO.getName());
            }
            riskNames.add(riskDTO.getName().toLowerCase());
        }
    }


}
