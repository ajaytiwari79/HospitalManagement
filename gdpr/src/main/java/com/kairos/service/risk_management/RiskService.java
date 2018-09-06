package com.kairos.service.risk_management;


import com.kairos.gdpr.data_inventory.RiskDTO;
import com.kairos.persistance.model.common.MongoBaseEntity;
import com.kairos.persistance.model.risk_management.Risk;
import com.kairos.persistance.repository.risk_management.RiskMongoRepository;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        List<Risk> risks = new ArrayList<>();
        Map<T, List<Risk>> riskListRelatedToObjectMap = new HashMap<>();
        risksRelatedToObject.forEach((objectToWhichRiskRelated, riskDTOList) -> {

            List<Risk> riskRelatedTOObject = buildRiskAtCountryLevel(countryId, riskDTOList);
            risks.addAll(riskRelatedTOObject);
            riskListRelatedToObjectMap.put(objectToWhichRiskRelated, riskRelatedTOObject);

        });
        riskMongoRepository.saveAll(getNextSequence(risks));
        Map<T, List<BigInteger>> objctAndRiskIdsMap = new HashMap<>();
        riskListRelatedToObjectMap.forEach((objectToWhichRiskRelated, riskList) -> {
            List<BigInteger> riskIdList = new ArrayList<>();
            riskList.forEach(risk -> riskIdList.add(risk.getId()));
            objctAndRiskIdsMap.put(objectToWhichRiskRelated, riskIdList);
        });

        return objctAndRiskIdsMap;

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
