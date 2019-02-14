package com.kairos.persistence.repository.risk_management;


import com.kairos.response.dto.common.RiskResponseDTO;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class RiskDaoImpl {

    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public List<RiskResponseDTO> getAllRiskOfOrganizationId(Long unitId){
        Query riskQuery = entityManager.createNamedQuery("getAllRiskData").setParameter(1, unitId);
        return (List<RiskResponseDTO>) riskQuery.getResultList();
    }
}
