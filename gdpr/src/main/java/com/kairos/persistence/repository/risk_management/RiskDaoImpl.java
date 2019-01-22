package com.kairos.persistence.repository.risk_management;


import com.kairos.response.dto.common.RiskResponseDTO;
import com.kairos.response.dto.common.riskresponseDTO;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.transform.Transformers;
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

    public List<RiskResponseDTO> getAllRiskOfOrganizationId(Long unitId){
        Query riskQuery = entityManager.createNamedQuery("getAllRiskData").setParameter(1, unitId);
        List<RiskResponseDTO> risks = riskQuery.getResultList();
        return risks;
    }
}
