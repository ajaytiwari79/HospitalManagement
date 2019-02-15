package com.kairos.persistence.repository.risk_management;


import com.kairos.response.dto.common.RiskResponseDTO;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class RiskDaoImpl {

    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public List<RiskResponseDTO> getAllRiskOfOrganizationId(Long unitId){
        Query assetTypeRiskQuery = entityManager.createNamedQuery("getAllAssetTypeRiskData").setParameter(1, unitId);
        Query processingActivityRiskQuery = entityManager.createNamedQuery("getAllProcessingActivityRiskData").setParameter(1, unitId);
        List<RiskResponseDTO> allRisks = new ArrayList<>();
        allRisks.addAll( assetTypeRiskQuery.getResultList());
        allRisks.addAll(processingActivityRiskQuery.getResultList());
        return allRisks;
    }
}
